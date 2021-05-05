package com.binary_studio.academy_coin;

import java.util.Arrays;
import java.util.stream.Stream;

public final class AcademyCoin {

    private AcademyCoin() {
    }

    public static int maxProfit(Stream<Integer> prices) {
        Integer[] pricesArr = prices.toArray(Integer[]::new);
        int pricesArrLength = pricesArr.length;
        if (pricesArrLength == 0) {
            return 0;
        }
        int maxResult = 0;
        for (int i = 0; i < pricesArrLength - 1; i++) {
            for (int j = i + 1; j < pricesArrLength; j++) {
                int diff = pricesArr[j] - pricesArr[i];
                if (diff > 0) {
                    maxResult = Math.max(maxResult, diff +
                            maxProfit(Arrays.stream(
                                    Arrays.copyOfRange(pricesArr, j + 1, pricesArrLength))));
                }
            }
        }
        return maxResult;
    }

}
