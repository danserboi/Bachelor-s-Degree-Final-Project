import random
from flask import current_app


def generate_reset_pin():
    system_random = random.SystemRandom()

    reset_pin = ""
    for i in range(current_app.config["RESET_PIN_LENGTH"]):
        reset_pin += str(system_random.randint(0, 9))

    return reset_pin
