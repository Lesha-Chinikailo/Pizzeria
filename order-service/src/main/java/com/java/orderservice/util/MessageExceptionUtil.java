package com.java.orderservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageExceptionUtil {
    public static final String UnableFindOrderById = "Unable to find order with id: %s";
    public static final String OrderAlreadyPaidWithId = "Order already is paid with id: %s";

}
