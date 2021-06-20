package com.scd.mvctest.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import io.swagger.models.Swagger;
import org.catdou.param.generate.config.SwaggerDataProvider;
import org.catdou.param.generate.constant.GenTypeEnum;
import org.catdou.param.generate.core.BaseGenerator;
import org.catdou.param.generate.factory.GenerateFactory;
import org.catdou.param.generate.model.GenerateParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import springfox.documentation.RequestHandler;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.service.Documentation;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.DocumentationPlugin;
import springfox.documentation.spi.service.RequestHandlerCombiner;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.DefaultConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.collect.FluentIterable.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.spi.service.contexts.Orderings.*;

/**
 * After an application context refresh, builds and executes all DocumentationConfigurer instances found in the
 * application context.
 *
 * If no instances DocumentationConfigurer are found a default one is created and executed.
 */
@Component
public class SwaggerDocConfig implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper.class);
    private static final String SPRINGFOX_DOCUMENTATION_AUTO_STARTUP = "springfox.documentation.auto-startup";
    private final DocumentationPluginsManager documentationPluginsManager;
    private final List<RequestHandlerProvider> handlerProviders;
    private final DocumentationCache scanned;
    private final ApiDocumentationScanner resourceListing;
    private final Environment environment;
    private final DefaultConfiguration defaultConfiguration;
    private final ServiceModelToSwagger2Mapper mapper;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    @Autowired(required = false)
    private List<AlternateTypeRuleConvention> typeConventions;

    @Autowired
    public SwaggerDocConfig (
            DocumentationPluginsManager documentationPluginsManager,
            List<RequestHandlerProvider> handlerProviders,
            DocumentationCache scanned,
            ApiDocumentationScanner resourceListing,
            TypeResolver typeResolver,
            Defaults defaults,
            ServletContext servletContext,
            Environment environment,
            ServiceModelToSwagger2Mapper mapper) {

        this.documentationPluginsManager = documentationPluginsManager;
        this.handlerProviders = handlerProviders;
        this.scanned = scanned;
        this.resourceListing = resourceListing;
        this.environment = environment;
        this.defaultConfiguration = new DefaultConfiguration(defaults, typeResolver, servletContext);
        this.mapper = mapper;
    }

    private DocumentationContext buildContext(DocumentationPlugin each) {
        return each.configure(defaultContextBuilder(each));
    }

    private void scanDocumentation(DocumentationContext context) {
        try {
            scanned.addDocumentation(resourceListing.scan(context));
        } catch (Exception e) {
            log.error(String.format("Unable to scan documentation context %s", context.getGroupName()), e);
        }
    }

    private DocumentationContextBuilder defaultContextBuilder(DocumentationPlugin plugin) {
        DocumentationType documentationType = plugin.getDocumentationType();
        List<RequestHandler> requestHandlers = from(handlerProviders)
                .transformAndConcat(handlers())
                .toList();
        List<AlternateTypeRule> rules = from(nullToEmptyList(typeConventions))
                .transformAndConcat(toRules())
                .toList();
        return documentationPluginsManager
                .createContextBuilder(documentationType, defaultConfiguration)
                .rules(rules)
                .requestHandlers(combiner().combine(requestHandlers));
    }

    private Function<AlternateTypeRuleConvention, List<AlternateTypeRule>> toRules() {
        return new Function<AlternateTypeRuleConvention, List<AlternateTypeRule>>() {
            @Override
            public List<AlternateTypeRule> apply(AlternateTypeRuleConvention input) {
                return input.rules();
            }
        };
    }

    private RequestHandlerCombiner combiner() {
        return new DefaultRequestHandlerCombiner();
    }

    private Function<RequestHandlerProvider, ? extends Iterable<RequestHandler>> handlers() {
        return new Function<RequestHandlerProvider, Iterable<RequestHandler>>() {
            @Override
            public Iterable<RequestHandler> apply(RequestHandlerProvider input) {
                return input.requestHandlers();
            }
        };
    }

    @Override
    public boolean isAutoStartup() {
        String autoStartupConfig =
                environment.getProperty(
                        SPRINGFOX_DOCUMENTATION_AUTO_STARTUP,
                        "true");
        return Boolean.valueOf(autoStartupConfig);
    }

    @Override
    public void stop(Runnable callback) {
        callback.run();
    }

    @Override
    public void start() {
        if (initialized.compareAndSet(false, true)) {
            log.info("Context refreshed");
            List<DocumentationPlugin> plugins = pluginOrdering()
                    .sortedCopy(documentationPluginsManager.documentationPlugins());
            log.info("Found {} custom documentation plugin(s)", plugins.size());
            Docket docket = new Docket(DocumentationType.SWAGGER_2);
            scanDocumentation(buildContext(docket));
            String groupName = "default";
            Documentation documentation = scanned.documentationByGroup(groupName);
            Swagger swagger = mapper.mapDocumentation(documentation);
            SwaggerDataProvider swaggerDataProvider = new SwaggerDataProvider(swagger);
            swaggerDataProvider.initSwaggerData();
            GenerateParam generateParam = new GenerateParam();
            generateParam.setParentPath("file");
            BaseGenerator baseGenerator = GenerateFactory.createGenerator(GenTypeEnum.XML_SWAGGER);
            baseGenerator.generate(generateParam);
        }
    }

    @Override
    public void stop() {
        initialized.getAndSet(false);
        scanned.clear();
    }

    @Override
    public boolean isRunning() {
        return initialized.get();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
