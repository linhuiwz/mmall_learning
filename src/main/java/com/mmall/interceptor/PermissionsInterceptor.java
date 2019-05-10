package com.mmall.interceptor;

import com.google.gson.Gson;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Created by cancan on 2017/8/21.
 */
public class PermissionsInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        boolean isPass = true;
        if (method.getAnnotation(TeacherPermission.class) != null) {
            isPass = isTeacher(request);
        }
        if (method.getAnnotation(StudentPermission.class) != null) {
            isPass = isStudent(request);
        }
        if (method.getAnnotation(AdminPermission.class) != null) {
            isPass = isAdmin(request);
        }
        if (method.getAnnotation(SelfPermission.class) != null) {
            isPass = isSelf(request);
        }
        if (!isPass) {//未授权，返回401信息
//            Gson gson = new Gson();
//            ResponseJson json = new ResponseJson();
//            json.setCode(UNAUTHORIZED.getCode());
//            json.setMessage(UNAUTHORIZED.getMessage());
//            response.setCharacterEncoding("UTF-8");
//            response.getWriter().write(gson.toJson(json));
        }
        return isPass;
    }

    private boolean isTeacher(HttpServletRequest request) {
        //判断是否是老师
    }

    private boolean isStudent(HttpServletRequest request) {
        //判断是否是学生
    }

    private boolean isAdmin(HttpServletRequest request) {
        //判断是否是管理员
    }

    private boolean isSelf(HttpServletRequest request) {
        //判断是否是自身
    }

}
