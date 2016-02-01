#include "ScoreManager.h"

// local headers
#include "GameObject.h"
#include "Brick.h"
#include "FileHandler.h"

ScoreManager::ScoreManager() {
	current_score_ = 0;
}

ScoreManager::~ScoreManager() {
}

void ScoreManager::AddScore(const GameObject& go, int current_level) {
	if (dynamic_cast<const Brick*>(&go)) {
		current_score_ += 10 * current_level;
	}
}

ScoreManager::vector_of_strings ScoreManager::GetHighScores() const {
	FileHandler high_scores_file(result_list_file_);

	std::vector<std::string> results;
	auto line = high_scores_file.ReadLineFromFile();

	if (line != "EOF") {
		do {
			results.push_back(line);

			line = high_scores_file.ReadLineFromFile();
		} while (line != "EOF");
	}

	return results;
}

void ScoreManager::WriteHighScores(const std::vector<std::string>& results) {
	FileHandler high_scores_file(result_list_file_);
	
	high_scores_file.WriteToFile(results);
}