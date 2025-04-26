from threading import Lock
import time

class StringAnalyzer:
    def __init__(self):
        self.counter = 0
        self.strings_list = []
        self.lock = Lock()

    def analyze(self, input_string: str):
        with self.lock:
            self.counter += 1
            self.strings_list.append(input_string)
            time.sleep(1)  # Simulate processing delay

            # Return different values based on call count
            if self.counter == 1:
                return 0.5
            elif self.counter == 2:
                return 0.55
            elif self.counter == 3:
                return 0.8
            else:
                return None  # or some default value

    def phrases(self):
        with self.lock:
            result = self.strings_list.copy()
            self.strings_list.clear()
            return result

# Create an instance of StringAnalyzer for use in Kotlin
analyzer_instance = StringAnalyzer()

# Wrapper functions to interact with the analyzer_instance
def analyze_string(input_string: str):
    return analyzer_instance.analyze(input_string)

def get_phrases():
    return analyzer_instance.phrases()