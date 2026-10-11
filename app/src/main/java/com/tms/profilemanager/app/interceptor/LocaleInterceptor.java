package com.tms.profilemanager.app.interceptor;

import com.tms.profilemanager.app.config.security.CurrentUserInfo;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static com.tms.profilemanager.app.utils.AuthenticationHolder.getCurrentUserInfo;


public class LocaleInterceptor implements HandlerInterceptor {

   private static final String LOCALE_HEADER_NAME = "Locale";

   @Override
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
      String locale = request.getHeader(LOCALE_HEADER_NAME);
      if (locale != null) {
         LocaleContextHolder.setLocale(new Locale(locale));
      } else if (getCurrentUserInfo().map(CurrentUserInfo::getLocale).isPresent()) {
         LocaleContextHolder.setLocale(getCurrentUserInfo().map(CurrentUserInfo::getLocale).get());
      } else {
         LocaleContextHolder.setLocale(Locale.getDefault());
      }
      return true;
   }

   @Override
   public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
   }
   
   @Override
   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
   }
}
