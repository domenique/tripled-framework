package eu.tripled.eventbus.autoconfigure;

import com.google.common.base.Splitter;
import eu.tripled.eventbus.EventSubscriber;
import eu.tripled.eventbus.annotation.EnableEventHandlerSupport;
import eu.tripled.eventbus.annotation.EventHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public class EventBusImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ApplicationContextAware {

  @Autowired
  private EventSubscriber eventSubscriber;
  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

    scanner.addIncludeFilter(new AnnotationTypeFilter(EventHandler.class));
    Map<String, Object> allAnnotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableEventHandlerSupport.class.getName());
    String basePackage = (String) allAnnotationAttributes.get("basePackage");


    for (BeanDefinition beanDefinition: scanner.findCandidateComponents(basePackage)) {
      beanDefinition.setScope("singleton");
      beanDefinition.setLazyInit(false);
      beanDefinition.setAutowireCandidate(true);


      String beanName = createBeanName(beanDefinition);
      registry.registerBeanDefinition(beanName, beanDefinition);

      registry.getBeanDefinition(beanName);


      //subscriber.subscribe();
    }

  }

  private String createBeanName(BeanDefinition beanDefinition) {
    List<String> strings = Splitter.on(".").splitToList(beanDefinition.getBeanClassName());
    String name  = StringUtils.uncapitalize(strings.get(strings.size() - 1));
    return name;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
