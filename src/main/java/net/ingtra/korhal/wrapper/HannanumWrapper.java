package net.ingtra.korhal.wrapper;


import kr.bydelta.koala.data.Morpheme;
import kr.bydelta.koala.data.Sentence;
import kr.bydelta.koala.data.Word;
import kr.bydelta.koala.hnn.Tagger;
import net.ingtra.korhal.TokensResponse;

import java.util.LinkedList;
import java.util.List;

public class HannanumWrapper {
    static Tagger tagger = new Tagger();


    public static TokensResponse tokenize(String text, List<String> args) throws Exception {
        TokensResponse.Builder builder = TokensResponse.newBuilder();

        List<Sentence> sentences = tagger.jTag(text);

        for (Sentence sentence : sentences) {
            for (Word word : scala.collection.JavaConversions.asJavaIterable(sentence)) {
                for (Morpheme morpheme : scala.collection.JavaConversions.asJavaIterable(word)) {
                    TokensResponse.Token token = TokensResponse.Token.newBuilder()
                            .setText(morpheme.surface())
                            .setPos(morpheme.rawTag())
                            .build();
                    builder.addTokens(token);
                }
            }
        }

        return builder.build();
    }

    public static void main(String[] args) throws Exception {
        tokenize("집에 가서 잠을 자고 싶다", new LinkedList<>());
    }
}