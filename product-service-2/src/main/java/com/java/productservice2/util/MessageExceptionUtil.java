package com.java.productservice2.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageExceptionUtil {
    public static final String UnableFindProductById = "Unable to find product with id: %s";
    public static final String ProductIsTakenWithId = "Product is taken with id: %s";
    public static final String CategoryNotFoundWithId = "Unable to find category with id: %s";
    public static final String CategoryNotFoundWithIdByAddProduct = "Unable to find category with id: %s. You entered wrong category id";
}
