/* Licensed under the Apache License, Version 2.0 (the "License");
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

package org.activiti.engine.impl.cmd;

import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.util.ClockUtil;
import org.activiti.engine.task.Event;


/**
 * @author Tom Baeyens
 */
public class AddCommentCmd extends NeedsActiveTaskCmd<Object> {

  private static final long serialVersionUID = 1L;
  
  protected String processInstanceId;
  protected String message;
  
  public AddCommentCmd(String taskId, String processInstanceId, String message) {
    super(taskId);
    this.processInstanceId = processInstanceId;
    this.message = message;
  }
  
  protected Object execute(CommandContext commandContext, TaskEntity task) {
    String userId = Authentication.getAuthenticatedUserId();
    CommentEntity comment = new CommentEntity();
    comment.setUserId(userId);
    comment.setType(CommentEntity.TYPE_COMMENT);
    comment.setTime(ClockUtil.getCurrentTime());
    comment.setTaskId(taskId);
    comment.setProcessInstanceId(processInstanceId);
    comment.setAction(Event.ACTION_ADD_COMMENT);
    
    String eventMessage = message.replaceAll("\\s+", " ");
    if (eventMessage.length()>163) {
      eventMessage = eventMessage.substring(0, 160)+"...";
    }
    comment.setMessage(eventMessage);
    
    comment.setFullMessage(message);
    
    commandContext
      .getCommentEntityManager()
      .insert(comment);
    
    return null;
  }
  
  @Override
  protected String getSuspendedTaskException() {
    return "Cannot add a comment to a suspended task";
  }
}
