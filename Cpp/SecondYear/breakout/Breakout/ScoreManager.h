#pragma once
#ifndef BREAKOUT_SCORE_MANAGER_
#define BREAKOUT_SCORE_MANAGER_

// C++ libraries
#include <vector>

// forward declarations
class GameObject;

/**
 * This header file declares the ScoreManager. The ScoreManager is responsible for keeping
 * track of the current score and creating and writing a resultlist to/from file.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class ScoreManager {
public:
	// consts
	static const int HIGH_SCORE_LIMIT = 5;

	// typedefs
	typedef std::vector<std::string> vector_of_strings;

	ScoreManager();
	~ScoreManager();

	// getters
	int GetScore() { return current_score_; }
	
	// adder
	void AddScore(const GameObject& go, int current_level);
	
	// cleaner
	void Reset() { current_score_ = 0; }

	vector_of_strings GetHighScores() const;
	void WriteHighScores(const std::vector<std::string>& results);
private:
	// consts
	const std::string result_list_file_ = "..//Resources//texts//high_scores.txt";

	int current_score_;
};

#endif

