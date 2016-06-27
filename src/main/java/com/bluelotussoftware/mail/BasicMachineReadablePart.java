/*
 * Copyright 2016 John Yeary <jyeary@bluelotussoftware.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bluelotussoftware.mail;

import com.bluelotussoftware.mail.arf.FeedbackType;

/**
 * This class provides the minimal information required by RFC-5965 for the
 * machine readable portion of the ARF.
 *
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.0
 */
public class BasicMachineReadablePart implements MachineReadablePart {

    @Override
    public String getPart(FeedbackType feedbackType) {
        return "Feedback-Type: " + feedbackType.name() + "\n"
                + "User-Agent: arf-message-generator/1.0\n"
                + "Version: 1\n";
    }

}
