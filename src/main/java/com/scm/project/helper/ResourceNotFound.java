package com.scm.project.helper;



public class ResourceNotFound extends RuntimeException{

    public ResourceNotFound(String mes){
        super(mes);
    }
    public ResourceNotFound(){
        super("Resource Not found");
    }
}
