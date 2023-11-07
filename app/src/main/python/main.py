import wikipediaapi
import requests

def getArticle(title, language):
    wiki_wiki = wikipediaapi.Wikipedia("WikiReader (https://github.com/EiS94/WikiReader)" ,language)
    page = wiki_wiki.page(title)
    return page.text

def getSectionCount(title, language):
    wiki_wiki = wikipediaapi.Wikipedia("WikiReader (https://github.com/EiS94/WikiReader)" ,language)
    page = wiki_wiki.page(title)
    return len(page.sections)

def getArticleBySections(title, language):
    wiki_wiki = wikipediaapi.Wikipedia("WikiReader (https://github.com/EiS94/WikiReader)" ,language)
    page = wiki_wiki.page(title)
    result = ""
    for section in page.sections:
        result += section.title + "\nh\n" + section.text
        if (len(section.sections) > 0):
            if len(section.text) > 0:
                result += "\nh\n"
            for subsec in section.sections:
                result += subsec.title + "\nhh\n" + subsec.text
                if (len(subsec.sections) > 0):
                    for subsubsec in subsec.sections:
                        result += subsubsec.title + "\nhhh\n" + subsubsec.text
                        if (len(subsec.sections) > 0):
                            for subsubsubsec in subsubsec.sections:
                                result += subsubsubsec.title + "\nhhhh\n" + subsubsubsec.text + "\n;ssss;"
                        result += "\n;sss;"
                result += "\n;ss;"
        result += "\n;s;"
    return result

def requestSuggestions(req):
    if req == "":
        return ""
    S = requests.Session()
    URL = "https://de.wikipedia.org/w/api.php"
    PARAMS = {
        "action": "opensearch",
        "namespace": "0",
        "search": req,
        "limit": "5",
        "format": "json"
    }
    R = S.get(url=URL, params=PARAMS)
    DATA = R.json()
    result = ""
    for suggestion in DATA[1]:
        result += suggestion + ";"
    return result


def requestSuggestionForBestResult(req):
    S = requests.Session()
    URL = "https://de.wikipedia.org/w/api.php"
    PARAMS = {
        "action": "opensearch",
        "namespace": "0",
        "search": req,
        "limit": "10",
        "format": "json"
    }
    R = S.get(url=URL, params=PARAMS)
    DATA = R.json()
    result = ""
    return DATA[1][0]

def getBestResult(title, language):
    return getArticleBySections(requestSuggestionForBestResult(title), language)