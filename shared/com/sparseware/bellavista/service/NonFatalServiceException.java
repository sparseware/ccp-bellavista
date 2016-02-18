package com.sparseware.bellavista.service;

import com.sparseware.bellavista.MessageException;

public class NonFatalServiceException extends MessageException{

  public NonFatalServiceException(String msg) {
    super(msg);
  }

}
