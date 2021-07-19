
def get_lower_case_name(text):
    lst = []
    for index, char in enumerate(text):
        if char.isupper() and index != 0:
            lst.append("_")
        lst.append(char)
    return "".join(lst).lower()


def transform_dict_key(old_dict):
    new_dict = {}
    for k, v in old_dict.items():
        new_dict[get_lower_case_name(k)] = v
    return new_dict
