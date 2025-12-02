package com.example.lab4.characters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab4.databinding.ItemCharacterBinding
import com.example.lab4.characters.model.Character

class CharacterAdapter(private val characters: List<Character>) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(characters[position])
    }

    override fun getItemCount() = characters.size

    class CharacterViewHolder(private val binding: ItemCharacterBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character) {
            binding.name.text = character.name.takeIf { it.isNotBlank() } ?: "Unknown"

            binding.culture.text = "Culture: ${character.culture.takeIf { it.isNotBlank() } ?: "N/A"}"
            binding.born.text = "Born: ${character.born.takeIf { it.isNotBlank() } ?: "N/A"}"


            binding.titles.visibility = if (character.titles.isNotEmpty() && character.titles.first().isNotBlank()) {
                binding.titles.text = "Titles: ${character.titles.joinToString(", ")}"
                View.VISIBLE
            } else {
                View.GONE
            }

            binding.aliases.visibility = if (character.aliases.isNotEmpty() && character.aliases.first().isNotBlank()) {
                binding.aliases.text = "Aliases: ${character.aliases.joinToString(", ")}"
                View.VISIBLE
            } else {
                View.GONE
            }

            binding.playedBy.visibility = if (character.playedBy.isNotEmpty() && character.playedBy.first().isNotBlank()) {
                binding.playedBy.text = "Played by: ${character.playedBy.joinToString(", ")}"
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}