package com.example.notizbloq_v2;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.HashSet;

public class Test_Utilities {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void tagsParsedCorrectly1() {
        String testString = "aijf aoijf ai #hallo jafi jifo jeijf aiwof jasefopa #ich. aef jio awe ehsiss .#hallo faijaweof .\n" +
                "#ich";
        HashSet<String> correctTags = new HashSet();
        correctTags.add("hallo");
        correctTags.add("ich");
        HashSet<String> tagsToCheck = Utilities.parseTagsFromText(testString);
        assertEquals(correctTags, tagsToCheck);
    }
}
