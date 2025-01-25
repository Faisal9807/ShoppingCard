package com.ecom.service.imp;



import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ecom.service.CommonService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service	
public class CommonServiceImp implements CommonService {

	@Override
	public void removeSession() {
		
		HttpServletRequest req= ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest();
		HttpSession session=req.getSession();
		session.removeAttribute("succMsg");
		session.removeAttribute("errorMsg");
	}

}
