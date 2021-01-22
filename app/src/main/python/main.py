import wikipediaapi

def wiki(title):
    wiki_wiki = wikipediaapi.Wikipedia('de')
    page_by = wiki_wiki.page(title)
    return str(page_by.text)