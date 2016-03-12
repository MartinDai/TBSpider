package com.martin.product.http;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class UserAgents {

    private static final String FILE_PATH = "/user_agent/user_agent.txt";

    private static final Object LOCK = new Object();
    private static String[] userAgents = null;

    public static String[] getUserAgents() throws IOException {
        if (userAgents != null) {
            return userAgents;
        }

        synchronized (LOCK) {
            if (userAgents != null) {
                return userAgents;
            }

            Set<String> userAgentSet = new HashSet<>();
            ClassPathResource resource = new ClassPathResource(FILE_PATH);
            InputStream input = resource.getInputStream();
            Scanner scanner = new Scanner(input);
            while (scanner.hasNext()) {
                String userAgent = scanner.nextLine();
                userAgentSet.add(userAgent);
            }
            input.close();

            userAgents = new String[userAgentSet.size()];
            userAgentSet.toArray(userAgents);
            return userAgents;
        }
    }

    public static String getRandomUserAgent() throws Exception {
        String[] userAgentArray = getUserAgents();

        Random random = new Random();
        int index = random.nextInt() % userAgentArray.length;
        if (index < 0) {
            index = -index;
        }

        return userAgentArray[index];
    }

}
