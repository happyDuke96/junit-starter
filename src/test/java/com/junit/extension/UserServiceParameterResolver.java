package com.junit.extension;

import com.junit.dao.UserDao;
import com.junit.service.UserService;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class UserServiceParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        //этот метод можно считать как условный если условия происходящей здесь вернет true,переходится в метод resolveParameter()
//        parameterContext.getIndex()
        return parameterContext.getParameter().getType() == UserService.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        //а здесь может получать параметры и настраивать по желанию либо проста вернуть его,new UserService() в этом случае для каждый тест вызывается один,т.е @BeforeEach инициализуется
        // в тесте вызывается,и потом новый создается,если у нас @BeforeEach(при этом не будет каждый раз новый создать ) отсутствует мы можем кэшировать параметры с помощью store
//        var store = extensionContext.getStore(ExtensionContext.Namespace.create(extensionContext.getTestMethod())); // здесь берется один объект для каждого тест из store
        var store = extensionContext.getStore(ExtensionContext.Namespace.create(UserService.class));
        return store.getOrComputeIfAbsent(UserService.class, obj -> new UserService(new UserDao()));
//        return store.getOrComputeIfAbsent(extensionContext.getTestMethod(), obj -> new UserService());
    }
}
