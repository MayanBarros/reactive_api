package com.webflux.reactiveapiwebflux.service;

import com.webflux.reactiveapiwebflux.entity.Emprestimo;
import com.webflux.reactiveapiwebflux.entity.Parcela;
import com.webflux.reactiveapiwebflux.exception.CpfCnpjNotValidException;
import com.webflux.reactiveapiwebflux.exception.DataDeNascimentoNotValidException;
import com.webflux.reactiveapiwebflux.exception.ValorNotValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmprestimoService {

    @Value("${tax.pattern.monthly}")
    private BigDecimal taxPatternMonthly;

    @Value("${tax.pattern.annual}")
    private BigDecimal taxPatternAnnual;

    BigDecimal jurosComRiscoAnual = BigDecimal.ZERO;
    BigDecimal jurosComRiscoMensal = BigDecimal.ZERO;

    private static final MathContext MATH_CONTEXT = new MathContext(6, RoundingMode.HALF_UP);
    private static final BigDecimal VALOR_MINIMO = new BigDecimal("100.00");
    private static final BigDecimal VALOR_MAXIMO = new BigDecimal("1000.00");

    private static final BigDecimal RISCO_18 = new BigDecimal("1.05");
    private static final BigDecimal RISCO_19_45 = new BigDecimal("1.02");
    private static final BigDecimal RISCO_46_69 = new BigDecimal("1.01");

    LocalDateTime data = LocalDateTime.now();

    @Autowired
    private ValidCpfCnpjService validCpfCnpjService;

    public List<Parcela> validaEmprestimo(Emprestimo emprestimo) throws ParseException {
        validaCpfCnpj(emprestimo);
        validarValor(emprestimo);
        this.jurosComRiscoAnual = jurosDeRiscoPorDataDeNascimento(emprestimo);
        this.jurosComRiscoMensal = BigDecimal.valueOf((Math.pow((1 + (jurosComRiscoAnual.doubleValue() / 100)), 1D / 12D)) - 1).setScale(6, RoundingMode.HALF_EVEN);
        return validarCarencia(emprestimo);
    }

    public void validaCpfCnpj(Emprestimo emprestimo) {
        var cpfCnpjIsNotValid = !(validCpfCnpjService.validCpfCnpj(emprestimo.getCpfCnpj()));
        if (cpfCnpjIsNotValid) throw new CpfCnpjNotValidException("CPF/CNPJ is not valid!");
    }

    private void validarValor(Emprestimo emprestimo) {
        boolean valorIsNotValid = !(emprestimo.getValor().compareTo(VALOR_MINIMO) >= 0 && emprestimo.getValor().compareTo(VALOR_MAXIMO) <= 0);
        if (valorIsNotValid) throw new ValorNotValidException("Informe um Valor entre R$ 100.00 e R$ 1000.00 !") ;
    }

    private BigDecimal jurosDeRiscoPorDataDeNascimento(Emprestimo emprestimo) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String format = formatter.format(emprestimo.getDataDeNascimento());
        var idade = calculaIdade(format, "dd-MM-yyyy");
        if (idade < 18) {
            throw new DataDeNascimentoNotValidException("Você tem menos de 18 anos e não pode fazer empréstimo!");
        } else if (idade == 18) {
            return taxPatternAnnual.multiply(RISCO_18);
        } else if (idade <= 45) {
            return taxPatternAnnual.multiply(RISCO_19_45);
        } else if (idade <= 69) {
            return taxPatternAnnual.multiply(RISCO_46_69);
        } else throw new DataDeNascimentoNotValidException("Você tem 70 anos e não pode fazer empréstimo!");
    }

    public static int calculaIdade(String dataNasc, String pattern) {
        DateFormat sdf = new SimpleDateFormat(pattern);
        Date dataNascInput = null;

        try {
            dataNascInput = sdf.parse(dataNasc);
        } catch (Exception ignored) {}

        Calendar dateOfBirth = new GregorianCalendar();
        assert dataNascInput != null;
        dateOfBirth.setTime(dataNascInput);
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);

        dateOfBirth.add(Calendar.YEAR, age);

        if (today.before(dateOfBirth)) {
            age--;
        }
        return age;
    }

    public List<Parcela> validarCarencia(Emprestimo emprestimo) {
        data = somaUmMesNaData(LocalDateTime.now());

        var carencia = emprestimo.getDiasParaPagar();
        var saldoDevedor = emprestimo.getValor();
        var parcelas = emprestimo.getParcelas();

        if (carencia != 0) {

            if (carencia == 30 || carencia == 60 || carencia == 90) {
                data = somaUmMesNaData(data);
                saldoDevedor = saldoDevedor.multiply(BigDecimal.ONE.add(jurosComRiscoMensal)).setScale(2, RoundingMode.HALF_EVEN);

                if (carencia == 60 || carencia == 90) {
                    data = somaUmMesNaData(data);
                    saldoDevedor = saldoDevedor.multiply(BigDecimal.ONE.add(jurosComRiscoMensal)).setScale(2, RoundingMode.HALF_EVEN);

                    if (carencia == 90) {
                        data = somaUmMesNaData(data);
                        saldoDevedor = saldoDevedor.multiply(BigDecimal.ONE.add(jurosComRiscoMensal)).setScale(2, RoundingMode.HALF_EVEN);
                    }
                }
            }
        }

        BigDecimal valorDaPrestacaoMensal = pmt(jurosComRiscoMensal, emprestimo.getParcelas(), saldoDevedor)
                .setScale(2, RoundingMode.HALF_DOWN);

        List<Parcela> parcelaList = new ArrayList<>();
        parcelaList.add(new Parcela(null, 0, LocalDateTime.now(), BigDecimal.ZERO, valorDaPrestacaoMensal, saldoDevedor, BigDecimal.ZERO));

        for (int i = 1; i <= parcelas; i++) {
            var saldoDevedorMesAnterior = parcelaList.get(i - 1).getSaldoDevedor();
            var juros = saldoDevedorMesAnterior.multiply(jurosComRiscoMensal).setScale(2, RoundingMode.HALF_EVEN);
            var saldoDevedorMesAtual = saldoDevedorMesAnterior.subtract(valorDaPrestacaoMensal).add(juros);
            var amortizacao = valorDaPrestacaoMensal.subtract(juros);

            if (!Objects.equals(parcelaList.get(0).getSaldoDevedor(), emprestimo.getValor())) {
                var prestacaoMensalSemJurosDeCarencia = emprestimo.getValor().divide(new BigDecimal(12), 2, RoundingMode.HALF_UP);
                amortizacao = prestacaoMensalSemJurosDeCarencia.subtract(juros);
            }

            if (i == parcelas && !saldoDevedorMesAtual.equals(BigDecimal.ONE)) {
                valorDaPrestacaoMensal = valorDaPrestacaoMensal.add(saldoDevedorMesAtual);
                saldoDevedorMesAtual = BigDecimal.ZERO;
            }

            parcelaList.add(new Parcela(null, i, data, juros, valorDaPrestacaoMensal, saldoDevedorMesAtual, amortizacao));
            data = somaUmMesNaData(data);
        }

        parcelaList.forEach(System.out::println);
        return parcelaList;
    }

    public BigDecimal pmt(BigDecimal jurosMensal, Integer meses, BigDecimal valorPresente) {
        BigDecimal resultado = BigDecimal.ZERO;
        if (jurosMensal.compareTo(BigDecimal.ZERO) == 0) {
            // Se o Juros for ZERO divide o valorPresente pelos meses...
            resultado =  valorPresente.negate(MATH_CONTEXT).divide(new BigDecimal(meses), RoundingMode.HALF_UP);
        } else {
            //jurosMensal = jurosMensal.divide(new BigDecimal("100"));
            // ( 1 + i )
            BigDecimal rateSum1 = jurosMensal.add(BigDecimal.ONE);
            resultado = valorPresente.multiply(rateSum1.pow(meses)).multiply(jurosMensal)
                        .divide(pow(BigDecimal.ONE.add(jurosMensal), meses).subtract(BigDecimal.ONE), MATH_CONTEXT);
        }
        return resultado;
    }

    private static BigDecimal pow(BigDecimal x, Integer n) {
        return x.pow(n, MATH_CONTEXT);
    }

    private LocalDateTime somaUmMesNaData(LocalDateTime data) {
        int ano = data.getYear();
        int mes = data.getMonthValue() + 1;
        
        if (mes == 13) {
            mes = 1;
            ano++;
        }
        return LocalDateTime.of(ano, mes, data.getDayOfMonth(), 0, 0);
    }
}
