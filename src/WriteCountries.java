import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class WriteCountries {
    private final int kKEY_DELAY = 5; //ms

    private Robot m_robot;
    private ArrayList<String> m_countries = new ArrayList<String>();
    private Map<Character, Integer> m_keyMap = new HashMap<>();

    private final String[] kBLACKLIST = {
        "Anguilla", "Aland Islands", "St. Barthelemy", "Caribbean Netherlands", "Cocos (Keeling) Islands", "Congo - Brazzaville", "Cook Islands", "Curacao",
        "Aruba", "Antarctica", "American Samoa", "Christmas Island", "Western Sahara", "Bermuda", "Bouvet Island", "Congo - Kinshasa", "Dominican Republic", "Falkland Islands",
        "Faroe Islands", "French Guiana", "Guernsey", "Gibraltar", "Greenland", "Guadeloupe", "South Georgia & South Sandwich Islands", "Guam", "Hong Kong SAR China", "Heard & McDonald Islands",
        "Isle of Man", "British Indian Ocean Territory", "St. Martin", "Macau SAR China", "Northern Mariana Islands", "Martinique", "Montserrat", "New Caledonia", "Norfolk Island", 
        "Niue", "French Polynesia", "St. Pierre & Miquelon", "Pitcairn Islands", "Puerto Rico", "Palestinian Territories", "Reunion", "St. Helena", "Svalbard & Jan Mayen", "Sint Maarten",
        "U.S. Outlying Islands", "British Virgin Islands", "U.S. Virgin Islands", "Wallis & Futuna", "Mayotte", "Jersey", "Cayman Islands", "Turks & Caicos Islands", "French Southern Territories",
        "Tokelau", "Vatican City"
    };

    public WriteCountries() {
        try { m_robot = new Robot(); } 
        catch (AWTException e) { e.printStackTrace(); }

        for (String countryCode : Locale.getISOCountries())
            m_countries.add(StringUtils.stripAccents(new Locale("en", countryCode).getDisplayCountry()));

        this.adjustCountryList();
        this.fillKeyMap();
        System.out.println("DEBUG - Country List: " + m_countries);
        System.out.println("DEBUG - Key Map: " + m_keyMap);
    }

    public void write() {
        final char[] stops = {'&', '('};
        for (String country : m_countries) {
            characterIterator:
            for (int strIdx = 0; strIdx < country.length(); strIdx++) {
                char currentChar = country.toLowerCase().charAt(strIdx);
                for (char c : stops) if (currentChar == c) {m_robot.keyPress(KeyEvent.VK_BACK_SPACE); break characterIterator; } 
                System.out.print(currentChar);
                m_robot.keyPress(getKey(currentChar));
                try { Thread.sleep(kKEY_DELAY); } catch (InterruptedException e) { e.printStackTrace(); }
            }
            System.out.println();
        }
        
    }

    private int getKey(Character letter) {
        return m_keyMap.get(letter);
    }

    private void fillKeyMap() {
        char letter = 'a';
        int index = 65; // 'a' keycode
        while (letter <= 'z') {
            m_keyMap.put(letter, index);
            letter++; index++;
        }
        m_keyMap.put(' ', 32);
        m_keyMap.put('-', 45);
        m_keyMap.put('.', 46);
    }

    //To match Jetpunks list
    private void adjustCountryList() {
        m_countries.remove(43); // Getting rid of that pesky Côte d'Ivoire (Cote d?Ivoire)

        for (String s : kBLACKLIST) 
            if (!m_countries.remove(s))
                System.out.println(s);

        m_countries.add("Ivory Coast");
        m_countries.add("Vatican"); // Vatican City causes it to trigger on Vatican
        m_countries.add("Congo"); // Triggers both DRC and Republic of the Congo
        m_countries.add("Kosovo"); // Doesn't have an ISO code apparently
        m_countries.add("Dominican Republic"); //This has to go after Dominca or it will trigger it

    }
}
