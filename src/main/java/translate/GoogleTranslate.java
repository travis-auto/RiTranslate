package translate;

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class GoogleTranslate
{
  static String TEST_JSON_DATA = null; // set to null to use real data from google

  public static String URL = "https://translate.google.com/translate_a/single?client=t&sl=%SL%&tl=%TL%&hl=en&dt=bd&dt=ex&dt=ld&dt=md&dt=qc&dt=rw&dt=rm&dt=ss&dt=t&dt=at&ie=UTF-8&oe=UTF-8&otf=2&ssel=0&tsel=0&tk=516155|367641&q=%TEXT%";

  /**
   * Returns all translations for the text in ranked order for the given
   * part-of-speech
   */
  public String[] translations(String text, String fromLang, String toLang, String pos)
  {
    
    String call = URL.replace("%SL%", fromLang).replace("%TL%", toLang).replace("%TEXT%", text);

    JSONArray array = resultArray(call); 

	// if the size of JSON file is smaller than 6, meaning that no translation available
    if (array == null || array.size() < 6) {
        return null;
    }

    List<String> list = new ArrayList<String>(); 

    JSONArray trans = (JSONArray) array.get(1);
    if (trans == null || trans.size() < 1)
    	throw new RuntimeException("Unexpected error: trans = " + trans + " array = " + array);

    for (int i = 0; i < trans.size(); i++)
    {
      if (pos != null) { // compare pos if we have one
        
          String targetPos = (String) ((ArrayList) trans.get(i)).get(0);
          
          if (targetPos != null && !pos.toLowerCase().equals(targetPos))
            continue;
      }
        
      JSONArray buffer = (JSONArray) ((JSONArray) trans.get(i)).get(1);
      
      if (buffer == null || buffer.size() < 1)
    	  throw new RuntimeException("Unexpected error: buffer="+buffer+" trans="+trans);
      
      for (int j = 0; j < buffer.size(); j++)
      {
        list.add((String) buffer.get(j));
      }
    }
    
    return list.toArray(new String[0]);
  }

  /**
   * Returns all examples for the text
   */
  //@SuppressWarnings("rawtypes")
  public String[] examples(String text)
  {

    String call = URL.replace("%SL%", "en").replace("%TL%", "en").replace("%TEXT%", text);
    JSONArray array = resultArray(call);

	// if the size of JSON file is smaller than 6, meaning that no translation available
    if (array.size() < 6) 
    {
      return null;
    }

    JSONArray trans = (JSONArray) ((JSONArray) array.get(array.size() - 2)).get(0);
    if (trans == null || trans.size() < 1)
    	throw new RuntimeException("Unexpected error: trans = " + trans + " array = " + array);

    String[] s = new String[trans.size()];
    if (trans.size() == 1)
    {
      s[0] = text;
      return s;
    }
    for (int i = 0; i < trans.size(); i++)
    {
      s[i] = (String) ( (ArrayList) trans.get(i) ).get(0);
      s[i] = s[i].replace("<b>", "").replace("</b>", "");
    }
    return s;
  }

  /**
   * Returns all glosses for the text/pos
   */
  @SuppressWarnings("rawtypes")
  public String[] glosses(String word, String pos)
  {

    String call = URL.replace("%SL%", "en").replace("%TL%", "en").replace("%TEXT%", word);
    JSONArray array = resultArray(call);

	// if the size of JSON file is smaller than 6, meaning that no translation available
    if (array.size() < 6)
    {
      return null;
    }

    JSONArray trans = ((JSONArray) array.get(5));
    if (trans == null || trans.size() < 1)
    	throw new RuntimeException("Unexpected error: trans = " + trans + " array = " + array);
    

    for (int i = 0; i < trans.size(); i++)
    {
      String temp = (String) ((ArrayList) trans.get(i)).get(0);

      String p = pos.toLowerCase();
      
      if (p.equals(temp))
      {
        JSONArray buffer = (JSONArray) ((JSONArray) trans.get(i)).get(1);
        
        if (buffer == null || buffer.size() < 1)
        	throw new RuntimeException("Unexpected error: buffer = " + buffer + " trans = " + trans);
        
        List<String> list = new ArrayList<String>();

        for (int j = 0; j < buffer.size(); j++)
        {
          if ( ( (ArrayList) buffer.get(j) ).size() > 2 )
          {
        	  list.add((String) ((ArrayList) buffer.get(j)).get(2));
          }
        }
        String[] s = new String[list.size()];
        list.toArray(s);

        return s;
      }
    }

    return null;
  }

  /**
   * Returns all see also for the text
   */
  public String[] seeAlso(String text)
  {

    String call = URL.replace("%SL%", "en").replace("%TL%", "en").replace("%TEXT%", text);
    JSONArray array = resultArray(call);

	// if the size of JSON file is smaller than 5, meaning that no translation available
    if (array.size() < 5)
    {
      return null;
    }

    JSONArray trans = (JSONArray) ( (JSONArray) array.get(array.size() - 1) ).get(0);
    if (trans == null || trans.size() < 1)
    	throw new RuntimeException("Unexpected error: trans = " + trans + " array = " + array);

    String[] s = new String[trans.size()];
    for (int i = 0; i < trans.size(); i++)
    {
      s[i] = (String) trans.get(i);
    }

    return s;
  }

  /**
   * Returns all definitions for the text/pos
   */
  @SuppressWarnings("rawtypes")
  public String[] definitions(String word, String pos)
  {

    String call = URL.replace("%SL%", "en").replace("%TL%", "en").replace("%TEXT%", word);
    JSONArray array = resultArray(call);

	// if the size of JSON file is smaller than 6, meaning that no translation available
    if (array.size() < 6)
    {
      return null;
    }

    JSONArray trans = ( (JSONArray) array.get(5) );
    if (trans == null || trans.size() < 1)
    	throw new RuntimeException("Unexpected error: trans = " + trans + " array = " + array);

    String p = pos.toLowerCase();
    String[] s;

    for (int i = 0; i < trans.size(); i++)
    {
      String temp = (String) ((ArrayList) trans.get(i)).get(0);

      if (p.equals(temp))
      {
        JSONArray buffer = (JSONArray) ((JSONArray) trans.get(i)).get(1);
        
        if (buffer == null || buffer.size() < 1)
        	throw new RuntimeException("Unexpected error: buffer = " + buffer + " trans = " + trans);
        
        s = new String[buffer.size()];

        for (int j = 0; j < buffer.size(); j++)
        {
          s[j] = (String) ((ArrayList) buffer.get(j)).get(0);
        }
        return s;
      }
    }

    return null;
  }

  /**
   * Returns all synonyms for the text/pos
   */
  @SuppressWarnings("rawtypes")
  public String[] synonyms(String word, String pos)
  {

    String call = URL.replace("%SL%", "en").replace("%TL%", "en").replace("%TEXT%", word);
    JSONArray array = resultArray(call);

	// if the size of JSON file is smaller than 6, meaning that no translation available
    if (array.size() < 6)
    {
      return null;
    }

    JSONArray trans = (JSONArray) array.get(4);
    if (trans == null || trans.size() < 1)
    	throw new RuntimeException("Unexpected error: trans = " + trans + " array = " + array);

    Object cmp = new Double(1.1);
    // check if this is Double type, if yes then that means no synonyms
    if (((ArrayList) trans.get(1)).get(0).getClass().equals(cmp.getClass()))
      return null;

    String p = pos.toLowerCase();

    for (int i = 0; i < trans.size(); i++)
    {
      String temp = (String) ((ArrayList) trans.get(i)).get(0);

      if (p.equals(temp))
      {

        JSONArray buffer = (JSONArray) ((JSONArray) trans.get(i)).get(1);
        
        if (buffer == null || buffer.size() < 1)
        	throw new RuntimeException("Unexpected error: buffer = " + buffer + " trans = " + trans);
        
        List<String> list = new ArrayList<String>();

        for (int j = 0; j < buffer.size(); j++)
        {
          JSONArray jsonarrayJ = (JSONArray) buffer.get(j);
          JSONArray jsonarrayK = (JSONArray) jsonarrayJ.get(0);

          for (int k = 0; k < jsonarrayK.size(); k++)
          {
            list.add((String) jsonarrayK.get(k));
          }
        }

        // remove duplicates from the list
        Set<String> set = new HashSet<String>(list);
        String[] s = new String[set.size()];
        set.toArray(s);

        return s;
      }
    }

    return null;
  }

  // -------------------------------------------------------------------------

  /**
   * Returns all translations for the text in ranked order by part-of-speech
   */
  public String[] translations(String text, String fromLang, String toLang)
  {
    return translations(text, fromLang, toLang, null);
  }

  /**
   * Returns the best translation for the text
   */
  public String translate(String text, String fromLang, String toLang)
  {

    String call = URL.replace("%SL%", fromLang).replace("%TL%", toLang).replace("%TEXT%", text);
    JSONArray array = resultArray(call);

    return array == null ? null
        : (((JSONArray) ((JSONArray) array.get(0)).get(0)).get(0)).toString();
  }

  private static JSONArray resultArray(String urlTocall)
  {
    SouperScraper scraper = new SouperScraper();
    scraper.ignoreContentType(true);

    String json = // TEST_JSON_DATA != null ? RiTa.loadString(TEST_JSON_DATA)
    scraper.connect(urlTocall).body();

    Object obj = JSONValue.parse(json);
    JSONArray array = (JSONArray) obj;


    return array;
  }

  public static void main(String[] args)
  {

  }
}
