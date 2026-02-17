package backend.ASP.service;

import backend.ASP.dto.EmailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmail(EmailDTO request) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String assunto = "Novo Orçamento Recebido";
        StringBuilder conteudo = new StringBuilder();

        conteudo.append("📝 Novo orçamento recebido!\n\n")
                .append("**Dados do Cliente:**\n")
                .append("Nome: ").append(request.nome()).append("\n")
                .append("Endereço: ").append(request.endereco()).append(", ").append(request.bairro()).append("\n")
                .append("Telefone: ").append(request.telefone()).append("\n")
                .append("CPF/CNPJ: ").append(request.cpfCnpj()).append("\n")
                .append("E-mail: ").append(request.email()).append("\n\n")
                .append("**Endereço de Entrega:**\n")
                .append(request.enderecoEntrega()).append(", ").append(request.bairroEntrega()).append("\n")
                .append("Data do Evento: ").append(request.dataEvento()).append("\n\n")
                .append("**Informações Extras:**\n")
                .append(request.infoExtras()).append("\n\n")
                .append("🛒 **Produtos no Carrinho:**\n");

        if (request.produtos() != null) {
            for (var produto : request.produtos()) {
                conteudo.append("🔹 ").append(produto.nome())
                        .append(" - Quantidade: ").append(produto.quantidade()).append("\n");
            }
        }

        helper.setFrom("thiagomvillas@gmail.com");
        helper.setTo("thiagomonteirovbs@gmail.com");
        helper.setSubject(assunto);
        helper.setText(conteudo.toString());

        mailSender.send(message);
    }
}
