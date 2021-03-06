﻿#pragma once

#include "Word.hpp"

namespace Flounder
{
	/// <summary>
	/// Represents a line of text during the loading of a text.
	/// </summary>
	class F_EXPORT Line
	{
	private:
		double m_maxLength;
		double m_spaceSize;

		std::vector<Word> m_words;
		double m_currentWordsLength;
		double m_currentLineLength;
	public:
		/// <summary>
		/// Creates a new line.
		/// </summary>
		/// <param name="spaceWidth"> The screen-space width of a space character. </param>
		/// <param name="maxLength"> The screen-space maximum length of a line. </param>
		Line(const double &spaceWidth, const double &maxLength);

		/// <summary>
		/// Deconstructor for the line.
		/// </summary>
		~Line();

		/// <summary>
		/// Attempt to add a word to the line. If the line can fit the word in without reaching the maximum line length then the word is added and the line length increased.
		/// </summary>
		/// <param name="word"> The word to try to add. </param>
		/// <returns> {@code true} if the word has successfully been added to the line. </returns>
		bool AddWord(const Word &word);

		double GetMaxLength() const { return m_maxLength; }

		double GetSpaceSize() const { return m_spaceSize; }

		std::vector<Word> GetWords() const { return m_words; }

		double GetCurrentWordsLength() const { return m_currentWordsLength; }

		double GetCurrentLineLength() const { return m_currentLineLength; }
	};
}
