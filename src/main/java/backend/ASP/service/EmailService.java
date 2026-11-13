package backend.ASP.service;

import backend.ASP.dto.EmailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String remetente;
    private final String destinatario;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.mail.from}") String remetente,
                        @Value("${app.mail.to}") String destinatario) {
        this.mailSender = mailSender;
        this.remetente = remetente;
        this.destinatario = destinatario;
    }

    public void enviarEmail(EmailDTO request) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        helper.setFrom(remetente);
        helper.setTo(destinatario);
        // Responder ao e-mail vai direto para o cliente.
        helper.setReplyTo(request.email());
        // Assunto fixo: dados do cliente nunca entram em cabeçalhos do e-mail.
        helper.setSubject("Novo orçamento recebido pelo site");
        helper.setText(montarCorpo(request));

        mailSender.send(message);
    }

    String montarCorpo(EmailDTO r) {
        StringBuilder c = new StringBuilder();
        c.append("Novo orçamento recebido pelo site\n\n");

        c.append("DADOS DO CLIENTE\n");
        linha(c, "Nome", r.nome());
        linha(c, "Telefone", r.telefone());
        linha(c, "E-mail", r.email());
        linha(c, "CPF/CNPJ", r.cpfCnpj());
        linha(c, "Endereço", juntar(r.endereco(), r.bairro()));

        c.append("\nENTREGA\n");
        linha(c, "Endereço", juntar(r.enderecoEntrega(), r.bairroEntrega()));
        linha(c, "Data do evento", r.dataEvento());

        if (temTexto(r.infoExtras())) {
            c.append("\nOBSERVAÇÕES\n").append(r.infoExtras().trim()).append("\n");
        }

        c.append("\nITENS DO ORÇAMENTO\n");
        if (r.produtos() == null || r.produtos().isEmpty()) {
            c.append("Nenhum item selecionado.\n");
        } else {
            for (var produto : r.produtos()) {
                c.append("- ").append(umaLinha(produto.nome()))
                        .append(" (quantidade: ").append(produto.quantidade()).append(")\n");
            }
        }
        return c.toString();
    }

    private void linha(StringBuilder c, String rotulo, String valor) {
        if (temTexto(valor)) {
            c.append(rotulo).append(": ").append(umaLinha(valor)).append("\n");
        }
    }

    private String juntar(String a, String b) {
        if (temTexto(a) && temTexto(b)) {
            return a.trim() + ", " + b.trim();
        }
        return temTexto(a) ? a : b;
    }

    private boolean temTexto(String valor) {
        return valor != null && !valor.isBlank();
    }

    // Campos de uma linha só: quebras de linha viram espaço, para ninguém forjar linhas no e-mail.
    private String umaLinha(String valor) {
        return valor.replaceAll("[\\r\\n]+", " ").trim();
    }
}