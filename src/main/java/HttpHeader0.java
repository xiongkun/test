/**
      *
      /*
      Your previous Plain Text content is preserved below:

      Part 1

      In an HTTP request, the Accept-Language header describes the list of
      languages that the requester would like content to be returned in. The header
      takes the form of a comma-separated list of language tags. For example:

      Accept-Language: en-US, fr-CA, fr-FR

      means that the reader would accept:

      1. English as spoken in the United States (most preferred)
      2. French as spoken in Canada
      3. French as spoken in France (least preferred)

      We're writing a server that needs to return content in an acceptable language
      for the requester, and we want to make use of this header. Our server doesn't
      support every possible language that might be requested (yet!), but there is a
      set of languages that we do support. Write a function that receives two arguments:
      an Accept-Language header value as a string and a set of supported languages,
      and returns the list of language tags that will work for the request. The
      language tags should be returned in descending order of preference (the
      same order as they appeared in the header).

      In addition to writing this function, you should use tests to demonstrate that it's
      correct, either via an existing testing system or one you create.

      Examples:

      parse_accept_language(
      "en-US, fr-CA, fr-FR",  # the client's Accept-Language header, a string
      ["fr-FR", "en-US"]      # the server's supported languages, a set of strings
      )
      returns: ["en-US", "fr-FR"]

      parse_accept_language("fr-CA, fr-FR", ["en-US", "fr-FR"])
      returns: ["fr-FR"]

      parse_accept_language("en-US", ["en-US", "fr-CA"])
      returns: ["en-US"]


      Part 2

      Accept-Language headers will often also include a language tag that is not
      region-specific - for example, a tag of "en" means "any variant of English". Extend
      your function to support these language tags by letting them match all specific
      variants of the language.

      Examples:

      parse_accept_language("en", ["en-US", "fr-CA", "fr-FR"])
      returns: ["en-US"]

      parse_accept_language("fr", ["en-US", "fr-CA", "fr-FR"])
      returns: ["fr-CA", "fr-FR"]

      parse_accept_language("fr-FR, fr", ["en-US", "fr-CA", "fr-FR"])
      returns: ["fr-FR", "fr-CA"]

      Part 3

      Accept-Language headers will sometimes include a "wildcard" entry, represented
      by an asterisk, which means "all other la‍‌‍‌‌‍‌‌‍‍‌‍‍‌‍‍‌‌nguages". Extend your function to
      support the wildcard entry.

      Examples:

      parse_accept_language("en-US, *", ["en-US", "fr-CA", "fr-FR"])
      returns: ["en-US", "fr-CA", "fr-FR"]

      parse_accept_language("fr-FR, fr, *", ["en-US", "fr-CA", "fr-FR"])
      returns: ["fr-FR", "fr-CA", "en-US"]

      */

import java.util.*;
import java.util.stream.*;
/*
Part 1
In an HTTP request, the Accept-Language header describes the list of
languages that the requester would like content to be returned in. The header
takes the form of a comma-separated list of language tags. For example:
Accept-Language: en-US, fr-CA, fr-FR
means that the reader would accept:
1. English as spoken in the United States (most preferred)
2. French as spoken in Canada
3. French as spoken in France (least preferred)
We're writing a server that needs to return content in an acceptable language
for the requester, and we want to make use of this header. Our server doesn't
support every possible language that might be requested (yet!), but there is a
set of languages that we do support. Write a function that receives two arguments:
an Accept-Language header value as a string and a set of supported languages,
and returns the list of language tags that that will work for the request. The
language tags should be returned in descending order of preference (the
same order as they appeared in the header).
In addition to writing this function, you should use tests to demonstrate that it's
correct, either via an existing testing system or one you create.
Examples:
parse_accept_language(
"en-US, fr-CA, fr-FR", # the client's Accept-Language header, a string
["fr-FR", "en-US"] # the server's supported languages, a set of strings
)
returns: ["en-US", "fr-FR"]
parse_accept_language("fr-CA, fr-FR", ["en-US", "fr-FR"])
returns: ["fr-FR"]
parse_accept_language("en-US", ["en-US", "fr-CA"])
returns: ["en-US"]
Part 2
Accept-Language headers will often also include a language tag that is not
region-specific - for example, a tag of "en" means "any variant of English". Extend
your function to support these language tags by letting them match all specific
variants of the language.
Examples:
parse_accept_language("en", ["en-US", "fr-CA", "fr-FR"])
returns: ["en-US"]
parse_accept_language("fr", ["en-US", "fr-CA", "fr-FR"])
returns: ["fr-CA", "fr-FR"]
parse_accept_language("fr-FR, fr", ["en-US", "fr-CA", "fr-FR"])
returns: ["fr-FR", "fr-CA"]
Part 3
Accept-Language headers will sometimes include a "wildcard" entry, represented
by an asterisk, which means "all other langua‍‍‌‍‌‌‌‍‌‌‍‍‍‍‌‌‌‌‍‌ges". Extend your function to
support the wildcard entry.
Examples:
parse_accept_language("en-US, *", ["en-US", "fr-CA", "fr-FR"])
returns: ["en-US", "fr-CA", "fr-FR"]
parse_accept_language("fr-FR, fr, *", ["en-US", "fr-CA", "fr-FR"])
returns: ["fr-FR", "fr-CA", "en-US"]

 */
public class HttpHeader0 {
    public List<String> parseAcceptLanguage(String input, Set<String> supported) {
        List<String> results = new ArrayList<String>();
        if (input == null || input.length() == 0) {
            return results;
        }

        String[] cultures = input.split(",\\s+");
        for (String culture : cultures) {
            if (setContains(supported, culture)) {
                results.add(culture);
            }
        }

        return results;
    }

    public List<String> parseAcceptLanguagePart2(String input, Set<String> supported) {
        List<String> results = new ArrayList<String>();
        if (input == null || input.length() == 0) {
            return results;
        }
        
        Map<String, Set<String>> supportedMap = new HashMap<>();
        for (String culture : supported) {
            String lang = culture.split("-")[0];
            supportedMap.putIfAbsent(lang, new HashSet<>());
            supportedMap.get(lang).add(culture);
        }

        String[] cultures = input.split(",\\s+");
        Set<String> seen = new HashSet<String>();
        for (String culture : cultures) {
            List<String> matches = tryGetMatches(supportedMap, culture);
            for (String match : matches) {
                if (seen.add(match)) {
                    results.add(match);
                }
            }
        }

        return results;
    }

    private List<String> tryGetMatches(Map<String, Set<String>> supportedMap, String culture) {
        List<String> results = new ArrayList<>();
        if (supportedMap == null || culture == null || culture.length() == 0) {
            return results;
        }

        String lang = culture.split("-")[0];
        if (lang.equals("*")) {
            return supportedMap.values().stream().flatMap(Set::stream).collect(Collectors.toList());
        } else if (!supportedMap.containsKey(lang)) {
            return results;
        }

        Set<String> cultures = supportedMap.get(lang);
        if (cultures.contains(culture)) {
            results.add(culture);
        } else if (culture.equals(lang)) {
            results.addAll(cultures);
        }
        return results;
    }



    private boolean setContains(Set<String> supported, String culture) {
        if (supported == null) {
            return false;
        } else if (supported.contains(culture)) {
            return true;
        }

        return supported.stream().anyMatch(culture::equalsIgnoreCase);
    }
}
