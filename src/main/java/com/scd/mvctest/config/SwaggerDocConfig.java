package com.scd.mvctest.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.scd.mvctest.business.model.UrlPath;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.models.parameters.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private RequestHandlerCombiner combiner;
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
            String swaggerGroup = null;
            String groupName = Optional.fromNullable(swaggerGroup).or("default");
            Documentation documentation = scanned.documentationByGroup(groupName);
            Swagger swagger = mapper.mapDocumentation(documentation);
            System.out.println(swagger);
            List<Tag> tagList = swagger.getTags();
            Map<String, Model> modelMap = swagger.getDefinitions();
            Map<String, Path> pathMap = swagger.getPaths();
            Map<String, List<UrlPath>> tagsPathMap = new HashMap<>();
            pathMap.forEach((key, path) -> {
                System.out.println(" url " + key);
                if (path.getGet() != null) {
                    putTagPathMap(tagsPathMap, key,"GET",  path.getGet());
                }
                if (path.getPost() != null) {
                    putTagPathMap(tagsPathMap, key,"POST", path.getPost());
                }
                if (path.getPut() != null) {
                    putTagPathMap(tagsPathMap, key,"PUT", path.getPut());
                }
                if (path.getDelete() != null) {
                    putTagPathMap(tagsPathMap, key,"DELETE", path.getDelete());
                }
            });
            // TODO 生成接口参数校验文件
            System.out.println(tagsPathMap);
        }
    }

    private void putTagPathMap(Map<String, List<UrlPath>> tagsPathMap, String key, String method, Operation operation) {
        List<String> tags = operation.getTags();
        List<Parameter> parameterList = operation.getParameters();
        if (!CollectionUtils.isEmpty(tags) && !CollectionUtils.isEmpty(parameterList)) {
            UrlPath urlPath = new UrlPath(key, method, parameterList);
            List<UrlPath> urlPathList = tagsPathMap.computeIfAbsent(tags.get(0), k -> new ArrayList<>());
            urlPathList.add(urlPath);
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
