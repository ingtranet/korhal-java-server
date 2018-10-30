package net.ingtra.korhal.wrapper;

import net.ingtra.korhal.TextsResponse;
import net.ingtra.korhal.TokensResponse;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.Sentence;

import java.util.List;
import java.util.stream.Collectors;

public class OpenKoreanTextWrapper {
    public static TokensResponse tokenize(String text, List<String> args) {
        TokensResponse.Builder builder = TokensResponse.newBuilder();
        boolean stem = args.contains("stem");

        List<KoreanTokenJava> tokens = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(
                OpenKoreanTextProcessorJava.tokenize(text)
        );

        for (KoreanTokenJava token: tokens) {
            TokensResponse.Token grpcToken = TokensResponse.Token.newBuilder()
                    .setText(stem ? token.getStem() : token.getText())
                    .setPos(token.getPos().name())
                    .build();
            builder.addTokens(grpcToken);
        }

        return builder.build();
    }

    public static TextsResponse normalize(String text) {
        TextsResponse.Builder builder = TextsResponse.newBuilder();

        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);
        builder.addTexts(normalized.toString());

        return builder.build();
    }

    public static TextsResponse splitSentence(String text, List<String> args) {
        List<Sentence> sentences = OpenKoreanTextProcessorJava.splitSentences(text);
        List<String> stringSentences = sentences.stream().map(sentence -> sentence.text()).collect(Collectors.toList());

        return TextsResponse.newBuilder().addAllTexts(stringSentences).build();
    }
}
