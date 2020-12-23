package com.webank.ai.fatecloud.config;

import com.webank.ai.fatecloud.Interceptor.AutoDeployInterceptor;
import com.webank.ai.fatecloud.Interceptor.ReferrerInterceptor;
import com.webank.ai.fatecloud.Interceptor.SiteAuthorizationInterceptor;
import com.webank.ai.fatecloud.Interceptor.UserInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {


    @Bean
    public AutoDeployInterceptor getAutoDeployInterceptor() {
        return new AutoDeployInterceptor();
    }


    @Bean
    public SiteAuthorizationInterceptor getSiteAuthorizationInterceptor() {
        return new SiteAuthorizationInterceptor();
    }

    @Bean
    public UserInterceptor getUserInterceptor() {
        return new UserInterceptor();
    }

    @Bean
    public ReferrerInterceptor getReferrerInterceptor() {
        return new ReferrerInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(getAutoDeployInterceptor())
                .addPathPatterns("/api/system/*");

        registry.addInterceptor(getSiteAuthorizationInterceptor())
                .addPathPatterns("/api/authority/*");

        registry.addInterceptor(getUserInterceptor())
                .addPathPatterns("/api/dropdown/**")

                .addPathPatterns("/api/authority/**")
                .excludePathPatterns("/api/authority/institutions", "/api/authority/institutions/approved", "/api/authority/apply", "/api/authority/applied", "/api/authority/check/partyId", "/api/authority/results")

                .addPathPatterns("/api/cloud/user/**")
                .excludePathPatterns("/api/cloud/user/login")

                .addPathPatterns("/api/fate/user/**")
                .excludePathPatterns("/api/fate/user/activate")

                .addPathPatterns("/api/function/**")
                .excludePathPatterns("/api/function/find/all/fateManager")

                .addPathPatterns("/api/group/**")

                .addPathPatterns("/api/federation/**")
                .excludePathPatterns("/api/federation/findOrganization")

                .addPathPatterns("/api/site/**")
                .excludePathPatterns("/api/site/findOneSite", "/api/site/checkAuthority", "/api/site/heart", "/api/site/page",
                        "/api/site/findOneSite/fateManager", "/api/site/checkAuthority/fateManager", "/api/site/heart/fateManager", "/api/site/page/fateManager",
                        "/api/site/checkUrl", "/api/site/activate", "/api/site/ip/accept", "/api/site/ip/query", "/api/site/fate/version"
                )

                .addPathPatterns("/api/system/**")
                .excludePathPatterns("/api/system/add")
                .excludePathPatterns("/api/system/heart")

                .addPathPatterns("/api/job/**")
                .excludePathPatterns("/api/job/push")

                .addPathPatterns("/api/product/**")
                .excludePathPatterns("/api/product/page/fatemanager")
        ;

//        registry.addInterceptor(getReferrerInterceptor());

    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**").allowedMethods("*").allowedOrigins("*").allowCredentials(true);
//    }

}
