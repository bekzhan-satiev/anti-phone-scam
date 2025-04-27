from threading import Lock
import time

class StringAnalyzer:
    def __init__(self):
        self.counter = 0
        self.strings_list = []
        self.lock = Lock()

    def analyze(self, input_string: str):
        with self.lock:
            # Increment the counter and add the input string to the list
            self.counter += 1
            self.strings_list.append(input_string)
            time.sleep(1)  # Simulate processing delay

            # Return different values based on call count
            if self.counter == 1:
                return []  # Return an empty list for the first call
            elif self.counter == 2:
                # For the second call, return the list of phrases and reset state
                result = self.strings_list.copy()
                self.strings_list.clear()  # Clear the list
                self.counter = 0  # Reset the counter
                return result
            else:
                return []  # Return an empty list for any other case

# Create an instance of StringAnalyzer for use in Kotlin
analyzer_instance = StringAnalyzer()

# Wrapper function to interact with the analyzer_instance
def analyze_string(input_string: str):
    return analyzer_instance.analyze(input_string)