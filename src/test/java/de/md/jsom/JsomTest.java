package de.md.jsom;

import static de.md.jsom.Jsom.*;

import junit.framework.TestCase;

/**
 *
 */
public class JsomTest extends TestCase {

    public Jsom test = map()
            .put("array", list(1, 2, 3))
            .put("lol", 2)
            .put("inner", map()
                    .put("key", "k")
                    .put("value", "v"));

    /**
     *
     */
    public void testGet() {
        assertEquals(2, test.get("lol").toInt());
        assertEquals(2, test.get("array").get(1).toInt());
        assertEquals("v", test.get("inner").get("value").toString());
        assertEquals("k", test.get("inner").get("key").toString());
        assertTrue(test.get("arrayz").isNull());
        assertEquals(3, list(1, 2, 3).get(2).toInt());
    }

    public void testReadme() {

        // Create a todo list
        Jsom todo = list(
                map()
                        .put("title", "learn java")
                        .put("done", true)
                        .put("year", 1999),
                map()
                        .put("title", "get jsom")
                        .put("done", false)
                        .put("year", 2017),
                map()
                        .put("title", "be awesome")
                        .put("done", false)
        );

        // Count things to do
        long count = todo.stream()
                .filter(item -> !$(item).get("done").toBoolean())
                .count();
        assertEquals(2, count);

        // Get maximum year
        int year = todo.stream()
                .filter(item -> !$(item).get("done").toBoolean())
                .mapToInt(item -> $(item).get("year").toInt())
                .max()
                .getAsInt();
        assertEquals(2017, year);

        // Find item containing awesomeness using a custom filter
        Jsom awesome = $(todo.stream()
                .filter(item -> $(item).get("title").toString().contains("awesome"))
                .findFirst()
                .get());
        assertEquals(valueOf(todo.get(2)), valueOf(awesome));

        // Check everything
        todo.stream().forEach(item -> $(item).put("done", true));
        todo.stream()
                .map(item -> $(item).get("done").toBoolean())
                .forEach(JsomTest::assertTrue);

    }

}
