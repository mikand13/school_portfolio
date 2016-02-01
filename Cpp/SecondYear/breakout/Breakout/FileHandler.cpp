#include "FileHandler.h"

FileHandler::FileHandler(std::string file_name) : 
	file_name_(file_name),
	file_in_(file_name_, std::ios::in) {}

FileHandler::~FileHandler() {
}

std::string FileHandler::ReadLineFromFile() {
	if (file_in_.is_open()) {
		std::string input;

		if (!file_in_.eof()) {
			std::getline(file_in_, input);
			return input;
		}
	}

	return "EOF";
}