package com.droveda;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SomeFunctionalInterfaceAndFunctionsTests {

    //reference -> https://www.baeldung.com/java-8-functional-interfaces

    public static void main(String[] args) {

        //functions
        Map<String, Integer> nameMap = new HashMap<>();
        nameMap.put("maria", "maria".length());

        var value = nameMap.computeIfAbsent("John", String::length);
        System.out.println(value);
        System.out.println(nameMap);

        Function<Integer, String> intToString = Object::toString;
        Function<String, String> quote = s -> "'" + s + "'";
        Function<Integer, String> quoteIntToString = quote.compose(intToString);
        System.out.println(quoteIntToString.apply(5));

        ShortToByteFunction function = s -> (byte) (s * 3);
        ShortToByteFunction function2 = s -> (byte) (s * 2);
        byte result = function.applyAsByte((short) 5);
        byte result2 = function2.applyAsByte((short) 5);
        System.out.println(result);
        System.out.println(result2);


        //Two-Arity Function Specializations
        Map<String, Integer> salaries = new HashMap<>();
        salaries.put("John", 40000);
        salaries.put("Freddy", 30000);
        salaries.put("Samuel", 50000);

        salaries.replaceAll((name, salary) -> name.equals("Samuel") ? salary - 100 : salary);
        salaries.replaceAll((name, oldValue) -> name.equals("Freddy") ? oldValue : oldValue + 10000);

        System.out.println(salaries);


        //Suppliers
        Supplier<Double> lazyValue = () -> {
            try {
                Thread.sleep(Duration.ofSeconds(3).toMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 9d;
        };

        Double valueSquared = squareLazy(lazyValue);
        System.out.println(valueSquared);


        //consumers
        List<String> names = Arrays.asList("John", "Freddy", "Samuel");
        names.forEach(name -> System.out.println("Hello, " + name));


        //consumers
        Map<String, Integer> ages = new HashMap<>();
        ages.put("John", 25);
        ages.put("Freddy", 23);
        ages.put("Samuel", 30);
        ages.forEach((name, age) -> System.out.println(name + " is " + age + " years old!"));


        //predicates
        List<String> names2 = List.of("Angela", "Aaron", "Bob", "Clarie", "David");
        var filtered = names2.stream()
                .filter(name -> name.startsWith("A"))
                .collect(Collectors.toList());
        System.out.println(filtered);

        Predicate<Integer> p = i -> i % 2 == 0;
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8);
        numbers.stream()
                .filter(p)
                .forEach(System.out::println);


        //operators
        //Operator interfaces are special cases of a function that receive and return the same value type. The UnaryOperator interface receives a single argument.
        List<String> names3 = Arrays.asList("bob", "josh", "megan");
        names3.replaceAll(String::toUpperCase);
        System.out.println(names3);

        List<Integer> values = List.of(3, 5, 8, 9, 12);
        int sum = values.stream()
                .reduce(0, (i1, i2) -> i1 + i2);
        System.out.println(sum);


        //Legacy Functional Interfaces
        Thread thread = new Thread(() -> {
            System.out.println("This is a new thread!");
        });
        thread.start();

        try {
            Thread.sleep(Duration.ofSeconds(2).toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static double squareLazy(Supplier<Double> lazyValue) {
        return Math.pow(lazyValue.get(), 2);
    }

}
