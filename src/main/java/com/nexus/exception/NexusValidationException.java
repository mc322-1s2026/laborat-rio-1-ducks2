package com.nexus.exception;

/**
 * Exceção de validação de regras de negócio do Nexus.
 * Deve ser lançada sempre que uma operação violar as regras
 * da máquina de estados ou de governança do sistema.
 */
public class NexusValidationException extends RuntimeException {
    public NexusValidationException(String message) {
        super(message);
    }
}
    