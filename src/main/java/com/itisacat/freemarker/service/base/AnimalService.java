package com.itisacat.freemarker.service.base;

import com.itisacat.freemarker.service.facade.IAnimalService;
import com.itisacat.freemarker.service.util.exception.CcException;

public abstract class AnimalService implements IAnimalService {
    @Override
    public String getSound()  {
        throw new CcException(-40000,"no implement");
    }
}
