#pragma once
#ifndef BREAKOUT_DELTA_TIMER_
#define BREAKOUT_DELTA_TIMER_

// C++ libraries
#include <chrono>

/**
 * This header file declares the DeltaTimer. It is loosely based on the timer shown in class
 * and the timer from Lazy Foo.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class DeltaTimer {
public:
	DeltaTimer() {}
	~DeltaTimer();

	void Start() {
		current_ = hr_clock::now();
		pause_time_ = hr_clock::now();
	}

	// update
	void Update() {
		auto last_frame_ = current_;
		current_ = hr_clock::now();
		auto time_span = 
			std::chrono::duration_cast<std::chrono::nanoseconds>(current_ - last_frame_);
		delta_time_ = static_cast<float>(time_span.count() / 1000000.f);
	}

	// getter for deltatime
	const float& GetDeltaTime() { return delta_time_; }

	// frame cap operations
	void Pause() { pause_time_ = hr_clock::now(); }
	void UnPause() { current_ += pause_time_ - current_; }
	void Reset() { current_ = hr_clock::now(); }
private:
	// typedefs
	typedef std::chrono::high_resolution_clock hr_clock;
	typedef hr_clock::time_point time_point;
	typedef hr_clock::duration duration;

	// deltatime
	float delta_time_;

	// deltatime use
	time_point current_;

	// framecap use
	time_point pause_time_;
};

#endif

