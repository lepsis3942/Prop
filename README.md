# Prop

A simple app for calculating how much to invest in different investments given an investment amount and a desired percentage of your portfolio for all current investments

Will dynamically allocate money where it needs to go to most quickly get you aligned (and stay aligned) with your deisred portfolio balance

## Implementation

This is a testbed app for messing around in Jetpack Compose and the Compose Navigation library.
NOTE: This was intended to be a small simple app. As such everything is contained in 1 gradle module to keep things simple. As requiremets change/grow, that would most certainly change.

Some other things I've used/tested here:
- Hilt for dependency injection
- Room for data persistence
- Kotlin coroutine Flow for asynchronous computaion

## Missing items/features
- Testing is important in codebases, this project is a personal project that I code in my free time. As such I have strategically written tests where most needed to get the most value from them while balancing the limited time I have to spend on this
- Would love to extend the financial API portion of the app, so far just a POC for stock prices. Would like to include historical/background data for research and current news per investment
