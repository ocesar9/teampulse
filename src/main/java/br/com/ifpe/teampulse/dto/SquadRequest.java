package br.com.ifpe.teampulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SquadRequest {
    // Campos para criação
    @NotBlank(message = "O nome da squad é obrigatório", groups = CreateGroup.class)
    @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres", groups = CreateGroup.class)
    private String name;

    @Size(min = 1, message = "Deve conter pelo menos um membro", groups = CreateGroup.class)
    private List<
            @NotBlank(message = "ID do membro não pode ser vazio")
            @Size(min = 36, max = 36, message = "ID do membro deve ter 36 caracteres")
                    String> memberIds;

    // Campos para atualização
    @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres", groups = UpdateGroup.class)
    private String newName;

    @Size(min = 1, message = "Deve especificar pelo menos uma operação", groups = UpdateGroup.class)
    private List<
            @NotBlank(message = "ID do membro não pode ser vazio")
            @Size(min = 36, max = 36, message = "ID do membro deve ter 36 caracteres")
                    String> membersToRemove;

    public interface CreateGroup {}
    public interface UpdateGroup {}
}