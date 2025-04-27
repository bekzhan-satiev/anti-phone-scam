from typing import List

import spacy

from setup import MODEL_PATH
from signatures import get_base_signatures, get_reason_signatures

nlp = spacy.load(MODEL_PATH)


def compare(left: str, right: str) -> float:
    return nlp(left).similarity(nlp(right))


base_signatures = get_base_signatures()
reason_signatures = get_reason_signatures()


scam_phrases = []
# signatures = get_base_signatures()
similarity_threshold = 0.6
scam_threshold = 3


def check_against_base_signatures(phrase: str) -> tuple:
    max_similarity = -1
    best_hit = ""
    for signature in base_signatures:
        similarity = compare(phrase, signature)
        if similarity > max_similarity:
            max_similarity = similarity
            best_hit = signature
        # print(phrase, " | ", signature, " | ", max_similarity)
    if max_similarity >= similarity_threshold:
        return phrase, best_hit, max_similarity
    else:
        return "", "", 0


def check_against_reason_signatures(phrase: str) -> tuple:
    result = [("", "", 0)]
    for reason, signatures in reason_signatures.items():
        max_similarity = -1
        best_hit = ""
        for signature in signatures:
            similarity = compare(phrase, signature)
            if similarity > max_similarity:
                max_similarity = similarity
                best_hit = signature
            # print(phrase, " | ", signature, " | ", max_similarity)
        if max_similarity >= similarity_threshold:
            result.append((phrase, best_hit, max_similarity))

    return max(result, key=lambda x: x[2])


def analyze(phrase: str) -> List[str]:
    global scam_phrases
    base_signature_check = check_against_base_signatures(phrase)
    reason_signature_check = check_against_reason_signatures(phrase)

    if max(base_signature_check[2], reason_signature_check[2]) >= similarity_threshold:
        # print(phrase)
        scam_phrases.append(phrase)

    if len(scam_phrases) >= 2:
        result = scam_phrases
        scam_phrases = []
        return result

    return []


def main():
    frauds_phrases = [
        "алло здравствуйте это служба безопасности банка",
        "меня зовут сергей викторович подскажите это мария петровна",
        "у нас возникла подозрительная операция по вашему счету",
        "вы в последние часы делали перевод на сумму 15500 рублей",
        "нет вы уверены что это не вы",
        "если это не вы, нам нужно заблокировать операцию",
        "для этого мне нужно подтвердить вашу личность",
        "отправьте нам деньги",
        "отлично, теперь я подтверждаю отмену перевода"
    ]
    for phrase in frauds_phrases:
        result = analyze(phrase)
        if len(result) != 0:
            print(result)
            break
        # break


def test():
    print(compare("это служба безопасности вашего банка отправьте нам деньги", "сотрудник службы безопасности вашего банка"))


if __name__ == "__main__":
    main()
    # test()
