package com.junit;

import com.junit.extension.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
        GlobalExtension.class,
        UserServiceParameterResolver.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
        ThrowableExtension.class
})
public abstract class TestBase {
}
