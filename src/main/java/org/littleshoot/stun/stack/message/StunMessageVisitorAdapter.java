package org.littleshoot.stun.stack.message;

import org.littleshoot.stun.stack.message.turn.AllocateErrorResponse;
import org.littleshoot.stun.stack.message.turn.AllocateRequest;
import org.littleshoot.stun.stack.message.turn.ConnectRequest;
import org.littleshoot.stun.stack.message.turn.ConnectionStatusIndication;
import org.littleshoot.stun.stack.message.turn.DataIndication;
import org.littleshoot.stun.stack.message.turn.SendIndication;
import org.littleshoot.stun.stack.message.turn.AllocateSuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter class for convenient creation of message visitor subclasses.  This
 * will log errors whenever something is visited that's not overidden.  If
 * a subclass should handle a message, it therefore must override the 
 * appropriate visit method.
 * 
 * @param <T> The class the visitor methods return.
 */
public class StunMessageVisitorAdapter<T>
    implements StunMessageVisitor<T>
    {

    private final Logger LOG = LoggerFactory.getLogger(
        StunMessageVisitorAdapter.class);

    @Override
    public T visitAllocateRequest(final AllocateRequest request)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", request);
        return null;
        }

    @Override
    public T visitBindingRequest(final BindingRequest request)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", request);
        return null;
        }

    @Override
    public T visitConnectRequest(final ConnectRequest request)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", request);
        return null;
        }

    @Override
    public T visitConnectionStatusIndication(
        final ConnectionStatusIndication indication)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", indication);
        return null;
        }

    @Override
    public T visitDataIndication(final DataIndication data)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", data);
        return null;
        }

    @Override
    public T visitSendIndication(final SendIndication request)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", request);
        return null;
        }

    @Override
    public T visitAllocateSuccessResponse(
        final AllocateSuccessResponse response)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", response);
        return null;
        }

    @Override
    public T visitAllocateErrorResponse(
        final AllocateErrorResponse response)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", response);
        return null;
        }

    @Override
    public T visitBindingSuccessResponse(
        final BindingSuccessResponse response)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", response);
        return null;
        }

    @Override
    public T visitBindingErrorResponse(
        final BindingErrorResponse response)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", response);
        return null;
        }

    @Override
    public T visitNullMessage(final NullStunMessage message)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", message);
        return null;
        }

    @Override
    public T visitCanceledMessage(final CanceledStunMessage message)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", message);
        return null;
        }

    @Override
    public T visitConnectErrorMesssage(final ConnectErrorStunMessage message)
        {
        LOG.info(getClass().getSimpleName() +
            " visiting unexpected message: {}", message);
        return null;
        }
    }
