package edu.pnu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import edu.pnu.domain.AssociationCode;
import edu.pnu.persistence.AssociationCodeRepository;

@Component
@ReadingConverter
public class StringToAssociationCodeConverter implements Converter<String, AssociationCode> {
    
    @Autowired
    private AssociationCodeRepository associationCodeRepository;

    @Override
    public AssociationCode convert(String source) {
        return associationCodeRepository.findById(source).orElse(null);
    }
}
