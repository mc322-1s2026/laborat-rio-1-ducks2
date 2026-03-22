package com.nexus.exception;

/**
 * Exceção de validação de regras de negócio do Nexus.
 * Deve ser lançada sempre que uma operação violar as regras
 * da máquina de estados ou de governança do sistema.
 */
public class NexusValidationException extends RuntimeException {

    /**
     * Cria uma nova exceção com a mensagem informada.
     * @param message descrição do erro de validação
     */
    public NexusValidationException(String message) {
        super(message);
    }

    /**
     * Cria uma nova exceção com mensagem e causa original.
     * @param message descrição do erro de validação
     * @param cause exceção raiz
     */
    public NexusValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
