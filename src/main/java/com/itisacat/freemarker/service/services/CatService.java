package com.itisacat.freemarker.service.services;

import com.itisacat.freemarker.service.base.AnimalService;
import org.springframework.stereotype.Service;

@Service
public class CatService extends AnimalService {
    public String getSound() {
        return "i am cat";
    }
}
