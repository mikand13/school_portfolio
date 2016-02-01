#pragma once
#ifndef BREAKOUT_FILE_HANDLER_
#define BREAKOUT_FILE_HANDLER_

// C++ libraries
#include <string>
#include <fstream>
#include <vector>

// local headers
#include "Debug.h"

/**
 * This header file declares the FileHandler. It is a class i made quickly to show i could do some
 * file I/O and show that i can make a template method. It is not optimal because of the close of
 * the filereader in the output method but this is mostly to show i get the idea of function
 * templates.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class FileHandler {
public:
	explicit FileHandler(std::string file_name);
	~FileHandler();

	// input
	std::string ReadLineFromFile();

	// output
	template <class T>
	void WriteToFile(const std::vector<T>& obj_to_write) {
		DEBUG("Closing input");
		file_in_.close();
		DEBUG("Opening output");
		std::ofstream file_out(file_name_, std::ios::out, std::ios::trunc);

		DEBUG("File output open: " << file_out.is_open());
		if (file_out.is_open()) {
			for (size_t i = 0; i < obj_to_write.size(); ++i) {
				file_out << obj_to_write[i];
				DEBUG("Writing to file: " << obj_to_write[i]);

				if (i < obj_to_write.size() - 1) {
					file_out << "\n";
				}
			}
		}

		file_out.close();
	}
private:
	std::string file_name_;
	std::ifstream file_in_;
};

#endif

