

def addIndentBlank(indent):
    space = ""
    for i in range(0,indent):
        space += "    "
    return space


def format_string(data):
    current = '\0'
    indent = 0
    # last = ""
    st_data = ""
    a = 0
    isInQuotationMarks = False
    if not data:
        return ""
    for i in data:
        last = current
        current = i
        if current == '"':
            if last !='\\':
                if isInQuotationMarks:
                    isInQuotationMarks = False
                else:
                    isInQuotationMarks = True
            st_data += current
            continue
        if current == '{' or current == '[':
            st_data += current
            if not isInQuotationMarks:
                st_data += '\n'
                indent = int(indent) + 1
                st_data += addIndentBlank(indent)
            continue
        if current == '}' or current == ']':
            if not isInQuotationMarks:
                st_data += '\n'
                indent -= 1
                st_data += addIndentBlank(indent)
            st_data += current
            continue
        if current == ',':
            st_data += current
            if not isInQuotationMarks and last != '\\':
                st_data += '\n'
                st_data += addIndentBlank(indent)
            continue
        if current == ' ':
            if data[a-1] != ',':
                st_data += current
            continue
        if current == '\\':
            st_data += '\\'
            continue
        else:
            st_data += current
    return st_data