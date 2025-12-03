package com.example.lab4.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab4.characters.model.AppDatabase
import com.example.lab4.databinding.FragmentCharactersBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CharactersFragment : Fragment() {

    private var _binding: FragmentCharactersBinding? = null

    private lateinit var characterAdapter: CharacterAdapter

    // 1. Создаем ViewModel с помощью кастомной фабрики
    private val viewModel: CharactersViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = CharacterRepository(database.characterDao())
        CharactersViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCharactersBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.let { binding ->
            setupRecyclerView(binding)
            setupListeners(binding)
            observeViewModel(binding)
        }
    }

    private fun setupRecyclerView(binding: FragmentCharactersBinding) {
        characterAdapter = CharacterAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = characterAdapter
    }

    private fun setupListeners(binding: FragmentCharactersBinding) {
        // 2. Логика для "Обновить" (свайп вниз)
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshCharacters(11) // Обновляем текущую (или дефолтную) страницу
            binding.swipeRefreshLayout.isRefreshing = false // Убираем иконку загрузки
        }

        // 3. Логика для "Загрузить еще"
        binding.loadMoreButton.setOnClickListener {
            viewModel.refreshCharacters(12) // Загружаем следующую страницу
        }
    }

    private fun observeViewModel(binding: FragmentCharactersBinding) {
        // 4. Подписываемся на Flow из ViewModel
        viewModel.characters
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { characters ->
                if (characters.isNotEmpty()) {
                    binding.errorMessage.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    characterAdapter.updateData(characters)
                } else {
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}