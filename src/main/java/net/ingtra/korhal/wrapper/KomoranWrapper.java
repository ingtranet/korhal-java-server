package net.ingtra.korhal.wrapper;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;
import net.ingtra.korhal.TokensResponse;

import java.util.List;

public class KomoranWrapper {
    private final static Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

    public static TokensResponse tokenize(String text, List<String> args) {
        TokensResponse.Builder builder = TokensResponse.newBuilder();

        List<Token> tokens = komoran.analyze(text).getTokenList();
        for (Token token : tokens) {
            TokensResponse.Token grpcToken = TokensResponse.Token.newBuilder()
                    .setText(token.getMorph())
                    .setPos(token.getPos())
                    .build();
            builder.addTokens(grpcToken);
        }

        return builder.build();
    }
}
