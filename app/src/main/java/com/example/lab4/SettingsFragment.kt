package com.example.lab4

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.lab4.characters.model.AppDatabase
import com.example.lab4.characters.model.Character
import com.example.lab4.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsDataStoreManager: SettingsDataStoreManager
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsDataStoreManager = SettingsDataStoreManager(requireContext())
        sharedPreferences = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)

        loadSettings()
        setupListeners()
        updateBackupInfo()
    }

    private fun loadSettings() {
        lifecycleScope.launchWhenStarted {
            // Загрузка всех настроек из DataStore
            launch {
                settingsDataStoreManager.nickname.collect { binding.nicknameEditText.setText(it) }
            }
            launch {
                settingsDataStoreManager.notifications.collect { binding.notificationsSwitch.isChecked = it }
            }
            launch {
                settingsDataStoreManager.theme.collect { theme ->
                    if (theme == "dark") {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        binding.themeSwitch.isChecked = true
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        binding.themeSwitch.isChecked = false
                    }
                }
            }
        }

        // Загрузка имени файла из SharedPreferences
        val backupFilename = sharedPreferences.getString("backup_filename", "backup")
        binding.backupFileNameEditText.setText(backupFilename)
    }

    private fun setupListeners() {
        // Automatic saving on change
        binding.nicknameEditText.doAfterTextChanged { text ->
            lifecycleScope.launch {
                settingsDataStoreManager.setNickname(text.toString())
            }
        }

        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingsDataStoreManager.setNotifications(isChecked)
            }
            Toast.makeText(requireContext(), "Настройки уведомлений сохранены", Toast.LENGTH_SHORT).show()
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                val theme = if (isChecked) "dark" else "light"
                settingsDataStoreManager.setTheme(theme)
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            Toast.makeText(requireContext(), "Тема изменена", Toast.LENGTH_SHORT).show()
        }

        binding.backupFileNameEditText.doAfterTextChanged { text ->
            val newName = text.toString()
            if (newName.isNotBlank()) {
                sharedPreferences.edit().putString("backup_filename", newName).apply()
            }
        }

        // Backup buttons
        binding.createBackupButton.setOnClickListener {
            lifecycleScope.launch {
                createBackup()
            }
        }
        binding.deleteBackupButton.setOnClickListener { deleteBackup() }
        binding.restoreBackupButton.setOnClickListener { restoreBackup() }
    }

    private suspend fun createBackup() {
        val dao = AppDatabase.getDatabase(requireContext()).characterDao()
        val dataToBackup = dao.getAll().first()

        if (dataToBackup.isEmpty()) {
            Toast.makeText(requireContext(), "Нет данных для сохранения. Сначала откройте список персонажей", Toast.LENGTH_LONG).show()
            return
        }

        val serializedData = serializeData(dataToBackup)

        try {
            val backupFile = getBackupFile()
            backupFile.writeText(serializedData)
            Toast.makeText(requireContext(), "Резервная копия создана", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Ошибка создания резервной копии: ${e.message}", Toast.LENGTH_LONG).show()
        }

        updateBackupInfo()
    }

    private fun getBackupFile(): File {
        val fileName = sharedPreferences.getString("backup_filename", "backup") + ".txt"
        return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName)
    }

    private fun updateBackupInfo() {
        val backupFile = getBackupFile()
        val internalBackupFile = File(requireContext().filesDir, "internal_backup.txt")

        if (backupFile.exists()) {
            binding.backupInfoTextView.text = "Резервная копия: ${backupFile.name}\nРасположение: ${backupFile.absolutePath}\nРазмер: ${backupFile.length()} байт"
            binding.createBackupButton.isEnabled = false
            binding.deleteBackupButton.isEnabled = true
            binding.restoreBackupButton.isEnabled = false
        } else {
            binding.backupInfoTextView.text = "Резервная копия отсутствует"
            binding.createBackupButton.isEnabled = true
            binding.deleteBackupButton.isEnabled = false
            binding.restoreBackupButton.isEnabled = internalBackupFile.exists()
        }
    }

    private fun deleteBackup() {
        val backupFile = getBackupFile()
        if (backupFile.exists()) {
            try {
                val internalBackup = File(requireContext().filesDir, "internal_backup.txt")
                backupFile.copyTo(internalBackup, true)
                backupFile.delete()
                Toast.makeText(requireContext(), "Резервная копия удалена (и сохранена внутри приложения)", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка удаления: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        updateBackupInfo()
    }

    private fun restoreBackup() {
        val internalBackup = File(requireContext().filesDir, "internal_backup.txt")
        if (internalBackup.exists()) {
            try {
                val externalBackup = getBackupFile()
                internalBackup.copyTo(externalBackup, true)
                internalBackup.delete()
                Toast.makeText(requireContext(), "Резервная копия восстановлена", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка восстановления: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        updateBackupInfo()
    }

    private fun serializeData(data: List<Character>): String {
        return data.joinToString(separator = "\n\n====================\n\n") {
            "Имя: ${it.name}\n" +
                "Культура: ${it.culture}\n" +
                "Родился: ${it.born}\n" +
                "Титулы: ${it.titles.joinToString()}\n" +
                "Псевдонимы: ${it.aliases.joinToString()}\n" +
                "Актёры: ${it.playedBy.joinToString()}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}