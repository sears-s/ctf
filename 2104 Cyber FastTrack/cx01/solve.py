from bip_utils.bip.bip39 import MnemonicFileReader
from bip_utils import Bip39MnemonicValidator, Bip39SeedGenerator, Bip32

# Initialization
words = MnemonicFileReader().m_words_list
seed = "nature midnight buzz toe sleep fence kiwi ivory excuse system "
seed_start = "131c553f7fb4127e7b2b346991dd92"

for first_word in words:

    # First word length expected to be four
    if len(first_word) != 4:
        continue
    for second_word in words:

        # Second word length expected to be 6
        if len(second_word) != 6:
            continue

        # Create the mnemonic and see if valid
        test_seed = seed + first_word + " " + second_word
        if not Bip39MnemonicValidator(test_seed).Validate():
            continue

        # Check if correct mnemonic
        seed_bytes = Bip39SeedGenerator(test_seed).Generate()
        if seed_bytes.hex().startswith(seed_start):
            print(test_seed)
            break
