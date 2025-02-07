/*
 * Copyright 2019 Ivan Pekov (MrIvanPlays)

 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.github.mrivanplays.poll.question;

import com.github.mrivanplays.poll.util.Voter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
public class Question {

    private final String identifier;
    private final String message;
    private final Collection<String> validAnswers;
    @Setter(AccessLevel.NONE)
    private Set<Voter> answered;

    public Question(String identifier, String message, Collection<String> validAnswers) {
        this(identifier, message, validAnswers, new HashSet<>());
    }

    private Question(String identifier, String message, Collection<String> validAnswers, Set<Voter> answered) {
        this.identifier = identifier;
        this.message = message;
        this.validAnswers = validAnswers;
        this.answered = answered;
    }

    public void addAnswer(UUID answerer, String whatAnswered) {
        Voter voter = new Voter(answerer, whatAnswered);
        addAnswer(voter);
    }

    public void addAnswer(Voter voter) {
        answered.add(voter);
    }

    public Optional<String> getAnswer(UUID answerer) {
        for (Voter voter : answered) {
            if (voter.getUuid().equals(answerer)) {
                return Optional.of(voter.getAnswered());
            }
        }
        return Optional.empty();
    }

    public Question duplicate() {
        return new Question(identifier, message, validAnswers, answered);
    }
}
