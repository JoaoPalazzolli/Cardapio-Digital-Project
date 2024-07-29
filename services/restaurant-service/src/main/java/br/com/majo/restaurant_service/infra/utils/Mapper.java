package br.com.majo.restaurant_service.infra.utils;

import org.modelmapper.ModelMapper;

import java.util.List;

public class Mapper {

    private static final ModelMapper mapper = new ModelMapper();

    public static <O, D> D parseObject(O origin, Class<D> destination){
        return mapper.map(origin, destination);
    }

    public static <O, D> List<D> parseListObject(List<O> origin, Class<D> destination){
        return origin.stream().map(x -> mapper.map(x, destination)).toList();
    }
}
