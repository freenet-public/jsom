package de.md.jsom;

import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Fluent, Java-style JSON view with all important methods from Map and List.
 *
 * Works on arbitrary structures of Map<String, Object>, List<Object>, String,
 * Number, Boolean, and null. Functional methods (forEach etc.) have been
 * omitted in favor of Streams. You should not "store" Jsom instances. Use them
 * as a temporary access layer. Object insertion methods always unwrap Jsom
 * instances before inserting them.
 *
 * The only possible exceptions are ClassCastExceptions and
 * NullPointerExceptions. If any of these are thrown, assumptions about the
 * given JSON structure were wrong.
 *
 * Not thread-safe.
 *
 * @see Map
 * @see List
 * @see Stream
 */
public class Jsom {

    /**
     * Create a JSON view of a value.
     *
     * @param value Any value.
     */
    public Jsom(Object value) {
        this.value = valueOf(value);
    }

    /**
     * @return
     */
    public boolean isMap() {
        return value instanceof Map;
    }

    /**
     * @return
     */
    public boolean isList() {
        return value instanceof List;
    }

    /**
     * @return
     */
    public boolean isString() {
        return value instanceof String;
    }

    /**
     * @return
     */
    public boolean isNumber() {
        return value instanceof Number;
    }

    /**
     * @return
     */
    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    /**
     * @return
     */
    public boolean isNull() {
        return value == null;
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap() {
        return (Map<String, Object>) require(value, "Cannot cast null to map");
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> toList() {
        return (List<Object>) require(value, "Cannot cast null to list");
    }

    /**
     * (non-Javadoc)
     *
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return value == null ? "null" : value.toString();
    }

    /**
     * @return
     */
    public long toLong() {
        return value == null ? 0 : (long) value;
    }

    /**
     * @return
     */
    public int toInt() {
        return value == null ? 0 : (int) value;
    }

    /**
     * @return
     */
    public double toDouble() {
        return value == null ? 0.0 : (double) value;
    }

    /**
     * @return
     */
    public boolean toBoolean() {
        return value == null ? false : (boolean) value;
    }

    /**
     * @param element
     * @return
     */
    public Jsom add(Object element) {
        toList().add(valueOf(element));
        return this;
    }

    /**
     * @param index
     * @param element
     * @return
     */
    public Jsom add(int index, Object element) {
        toList().add(index, valueOf(element));
        return this;
    }

    /**
     * @param elements
     * @return
     */
    public Jsom addAll(Collection<Object> elements) {
        List<Object> asList = toList();
        elements.forEach((element) -> {
            asList.add(valueOf(element));
        });
        return this;
    }

    /**
     * @param index
     * @param elements
     * @return
     */
    public Jsom addAll(int index, Collection<Object> elements) {
        List<Object> asList = toList();
        for (Object element : elements) {
            asList.add(index++, valueOf(element));
        }
        return this;
    }

    /**
     * @return
     */
    public Jsom clear() {
        if (value instanceof Map) {
            toMap().clear();
        } else if (value instanceof List) {
            toList().clear();
        } else {
            throw new ClassCastException("Cannot clear() " + typeOf(value));
        }
        return this;
    }

    /**
     * @param object
     * @return
     */
    public boolean contains(Object object) {
        return toList().contains(valueOf(object));
    }

    /**
     * @param collection
     * @return
     */
    public boolean containsAll(Collection<Object> collection) {
        return collection.stream().noneMatch((element) -> (!toList().contains(valueOf(element))));
    }

    /**
     * @param key
     * @return
     */
    public boolean containsKey(String key) {
        return toMap().containsKey(key);
    }

    /**
     * Get value at key.
     *
     * @see Map#get(Object)
     * @param key
     * @return
     */
    public Jsom get(String key) {
        return $(toMap().get(key));
    }

    /**
     * Get element at index.
     *
     * @see List#get(int)
     * @param index
     * @return
     */
    public Jsom get(int index) {
        return $(toList().get(index));
    }

    /**
     * Search for element and return first index if found, or -1 otherwise.
     *
     * @see List#indexOf(Object)
     * @param element
     * @return
     */
    public int indexOf(Object element) {
        return toList().indexOf(valueOf(element));
    }

    /**
     * Return whether this map or list is empty.
     *
     * @see Map#isEmpty()
     * @see List#isEmpty()
     * @return
     */
    public boolean isEmpty() {
        if (value instanceof Map) {
            return toMap().isEmpty();
        } else if (value instanceof List) {
            return toList().isEmpty();
        } else {
            throw new ClassCastException("Cannot check if " + typeOf(value) + " is empty");
        }
    }

    /**
     * Return keys as a list.
     *
     * @return
     */
    public Jsom keys() {
        return $(Arrays.asList(toMap().keySet().toArray()));
    }

    /**
     * Search for element and return last index if found, or -1 otherwise.
     *
     * @see List#lastIndexOf(Object)
     * @param element
     * @return
     */
    public int lastIndexof(Object element) {
        return toList().lastIndexOf(valueOf(element));
    }

    /**
     * Insert value at key.
     *
     * @see Map#put(Object, Object)
     * @param key
     * @param value
     * @return
     */
    public Jsom put(String key, Object value) {
        toMap().put(key, valueOf(value));
        return this;
    }

    /**
     * Insert all entries from another map.
     *
     * @see Map#putAll(Map)
     * @param map
     * @return self
     */
    public Jsom putAll(Map<String, Object> map) {
        Map<String, Object> asMap = toMap();
        map.entrySet().forEach((entry) -> {
            asMap.put(entry.getKey(), valueOf(entry.getValue()));
        });
        return this;
    }

    /**
     * Insert value at key unless key exists.
     *
     * @see Map#putIfAbsent(Object, Object)
     * @param key
     * @param value
     * @return self
     */
    public Jsom putIfAbsent(String key, Object value) {
        toMap().putIfAbsent(key, valueOf(value));
        return this;
    }

    /**
     * Insert all entries from another map unless the respective keys exist.
     *
     * @param map
     * @return self
     */
    public Jsom putAllIfAbsent(Map<String, Object> map) {
        Map<String, Object> asMap = toMap();
        map.entrySet().forEach((entry) -> {
            asMap.putIfAbsent(entry.getKey(), valueOf(entry.getValue()));
        });
        return this;
    }

    /**
     * Remove an entry or a String element.
     *
     * @see Map#remove(Object)
     * @see List#remove(Object)
     * @param key
     * @return self
     */
    public Jsom remove(String key) {
        if (value instanceof Map) {
            toMap().remove(key);
        } else if (value instanceof List) {
            toList().remove(key);
        } else {
            throw new ClassCastException("Cannot remove from " + typeOf(value));
        }
        return this;
    }

    /**
     * Remove the element at the specified index.
     *
     * @see List#remove(int)
     * @param index
     * @return
     */
    public Jsom remove(int index) {
        toList().remove(index);
        return this;
    }

    /**
     * Remove an element.
     *
     * @see List#remove(Object)
     * @param element
     * @return
     */
    public Jsom remove(Object element) {
        toList().remove(valueOf(element));
        return this;
    }

    /**
     * Replace the entry at key with value.
     *
     * @param key
     * @param value
     * @return
     */
    public Jsom replace(String key, Object value) {
        toMap().replace(key, valueOf(value));
        return this;
    }

    /**
     * Replace the entry at key with newValue if the entry matches oldValue.
     *
     * @see Map#replace(Object, Object, Object)
     * @param key
     * @param oldValue
     * @param newValue
     * @return
     */
    public Jsom replace(String key, Object oldValue, Object newValue) {
        toMap().replace(key, valueOf(oldValue), valueOf(newValue));
        return this;
    }

    /**
     * Throw NullPointerException if this value is null.
     *
     * @return
     */
    public Jsom require() {
        require(value, "This value is required.");
        return this;
    }

    /**
     * Set an element at the specified index.
     *
     * @see List#set(int, Object)
     * @param index
     * @param element
     * @return
     */
    public Jsom set(int index, Object element) {
        toList().set(index, valueOf(element));
        return this;
    }

    /**
     * Return size of map or list.
     *
     * @see Map#size()
     * @see List#size()
     * @return
     */
    public int size() {
        if (value instanceof Map) {
            return toMap().size();
        } else if (value instanceof List) {
            return toList().size();
        } else {
            throw new ClassCastException("Cannot get size of " + typeOf(value));
        }
    }

    /**
     * Sort by specified comparator.
     *
     * @see List#sort(Comparator)
     * @param comparator
     * @return self
     */
    public Jsom sort(Comparator<Object> comparator) {
        toList().sort(comparator);
        return this;
    }

    /**
     * Return a sub list.
     *
     * @see List#subList(int, int)
     * @param from
     * @param to
     * @return
     */
    public Jsom subList(int from, int to) {
        return $(toList().subList(from, to));
    }

    /**
     * @see List#toArray()
     * @return
     */
    public Object[] toArray() {
        return toList().toArray();
    }

    /**
     * Get elements as an array of type T.
     *
     * @param <T>
     * @param array
     * @return
     */
    public <T> T[] toArray(T[] array) {
        return toList().toArray(array);
    }

    /**
     * Return map values.
     *
     * @return
     */
    public Jsom values() {
        return $(toMap().values());
    }

    /**
     * Create an element stream.
     *
     * @return
     */
    public Stream<Object> stream() {
        return toList().stream();
    }

    /**
     * Create a parallel element stream.
     *
     * @return
     */
    public Stream<Object> parallelStream() {
        return toList().parallelStream();
    }

    /**
     * Create a stream of map entries.
     *
     * @return
     */
    public Stream<Entry<String, Object>> entryStream() {
        return toMap().entrySet().stream();
    }

    /**
     * Create a parallel stream of map entries.
     *
     * @return
     */
    public Stream<Entry<String, Object>> parallelEntryStream() {
        return toMap().entrySet().parallelStream();
    }

    /**
     * The wrapped value.
     */
    protected final Object value;

    /**
     * Create a new Jsom instance from any value.
     *
     * @param value
     * @return
     */
    public static Jsom $(Object value) {
        return value instanceof Jsom ? (Jsom) value : new Jsom(value);
    }

    /**
     * Create a new Map (HashMap).
     *
     * @return
     */
    public static Jsom map() {
        return $(new HashMap<>());
    }

    /**
     * Create a new List (ArrayList).
     *
     * @param elements Initial elements
     * @return
     */
    public static Jsom list(Object... elements) {
        // manual copying here because of value unwrapping
        Jsom list = $(new ArrayList<>(elements.length));
        for (Object element : elements) {
            list.add(element);
        }
        return list;
    }

    /**
     * Deep clones an arbitrary value (Jsom or otherwise) as a new Jsom
     * instance.
     *
     * Only Maps and Lists are actually copied. Object arrays are transformed
     * into ArrayList.
     *
     * @param value
     * @return
     */
    public final static Jsom deepClone(Object value) {
        value = valueOf(value);
        if (value instanceof Object[]) {
            value = Arrays.asList((Object[]) value);
        }
        Jsom json = $(value);
        if (json.isList()) {
            return $(json.stream().map(Jsom::deepClone).collect(TO_LIST));
        } else if (json.isMap()) {
            return $(json.entryStream().map(Jsom::deepClone).collect(TO_MAP));
        }
        return json;
    }

    /**
     * Deep clone a Map Entry.
     *
     * @param entry
     * @return
     */
    public final static Entry<String, Object> deepClone(Entry<String, Object> entry) {
        return new SimpleEntry<>(entry.getKey(), deepClone(entry.getValue()));
    }

    /**
     * Return the JSON type of a value.
     *
     * @param value
     * @return
     */
    public static String typeOf(Object value) {
        value = valueOf(value);
        if (value instanceof Map) {
            return "map";
        } else if (value instanceof List) {
            return "list";
        } else if (value instanceof String) {
            return "string";
        } else if (value instanceof Number) {
            return "number";
        } else if (value instanceof Boolean) {
            return "boolean";
        } else if (value == null) {
            return "null";
        } else {
            return "unknown";
        }
    }

    /**
     * Unwrap a Jsom instance or return the value itself.
     *
     * @param value
     * @return A non-Jsom object
     */
    public static Object valueOf(Object value) {
        return value instanceof Jsom ? ((Jsom) value).value : value;
    }

    /**
     * Throw a custom NullPointerException if the specified value is null.
     *
     * @param <T>
     * @param value
     * @param message
     * @return
     */
    public static <T> T require(T value, String message) {
        if (valueOf(value) == null) {
            throw new NullPointerException(message);
        }
        return value;
    }

    /**
     * Return a predicate that matches entries against the specified keys.
     *
     * @param keys
     * @return
     */
    public static Predicate<Entry<String, Object>> keys(String... keys) {
        final List<String> list = Arrays.asList(require(keys, "Key array must not be null"));
        return entry -> list.contains(require(entry, "Cannot get key of null entry").getKey());
    }

    /**
     * A collector that generates Maps (HashMap) from entry streams.
     */
    public final static Collector<Entry<String, Object>, ?, Map<String, Object>> TO_MAP
            = Collectors.toMap(Entry::getKey,
                    e -> valueOf(e.getValue()),
                    (Object a, Object b) -> b,
                    HashMap::new
            );

    /**
     * A collector that generates Lists (ArrayList) from object streams.
     */
    public final static Collector<Object, ?, List<Object>> TO_LIST
            = new ArrayListCollector();

    /**
     * Extra class required because Collectors.toCollection( ArrayList::new )
     * does not allow valueOf()
     */
    private static class ArrayListCollector implements Collector<Object, List<Object>, List<Object>> {

        @Override
        public Supplier<List<Object>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<Object>, Object> accumulator() {
            return (list, value) -> list.add(valueOf(value));
        }

        @Override
        public BinaryOperator<List<Object>> combiner() {
            return (a, b) -> {
                a.addAll(b);
                return a;
            };
        }

        @Override
        public Function<List<Object>, List<Object>> finisher() {
            return list -> list;
        }

        @Override
        public Set<Collector.Characteristics> characteristics() {
            return CHARACTERISTICS;
        }

        private final static Set<Collector.Characteristics> CHARACTERISTICS
                = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));

    }

};
