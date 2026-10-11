package com.tms.profilemanager.app.entity.audit;

import com.tms.profilemanager.app.config.security.CurrentUserInfo;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity cre = (CustomRevisionEntity) revisionEntity;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUserInfo user) {
            cre.setUsername(user.getFullName());
            cre.setUserId(user.getUserId());
        }

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null && attrs.getRequest() != null) {
            cre.setUserIp(attrs.getRequest().getHeader("x-original-forwarded-for"));
        }
    }
}

